package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.GoogleAuthRequestDto;
import me.risinu.jobportal.dto.LoginResponseDto;
import me.risinu.jobportal.dto.UsersDto;
import me.risinu.jobportal.service.UsersService;
import me.risinu.jobportal.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;
    private final JWT tokenGenerator;

    @Autowired
    public UsersController(UsersService usersService, JWT tokenGenerator) {
        this.usersService = usersService;
        this.tokenGenerator = tokenGenerator;
    }

    private boolean isTokenValid(String token) {
        return tokenGenerator.verifyToken(token);
    }

    private boolean verifyTokenOwnedByUser(String token, int userId) {
        return tokenGenerator.verifyTokenOwnedByUser(token, userId);
    }

    @GetMapping
    public ResponseEntity<List<UsersDto>> getAllUsers(@RequestHeader("Authorization") String token) {
        if (!isTokenValid(token)) {
            return ResponseEntity.status(401).build();
        }
        List<UsersDto> users = usersService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersDto> getUserById(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyTokenOwnedByUser(token, id)) {
            return ResponseEntity.status(403).build();
        }
        UsersDto user = usersService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UsersDto> createUser(@RequestHeader("Authorization") String token, @RequestBody UsersDto usersDto) {
        if (!isTokenValid(token)) {
            return ResponseEntity.status(401).build();
        }
        UsersDto createdUser = usersService.createUser(usersDto);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsersDto> updateUser(@RequestHeader("Authorization") String token, @PathVariable int id, @RequestBody UsersDto usersDto) {
        if (!verifyTokenOwnedByUser(token, id)) {
            return ResponseEntity.status(403).build();
        }
        UsersDto updatedUser = usersService.updateUser(id, usersDto);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyTokenOwnedByUser(token, id)) {
            return ResponseEntity.status(403).build();
        }
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UsersDto> registerUser(@RequestBody UsersDto usersDto) {
        UsersDto registeredUser = usersService.registerUser(usersDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsersDto usersDto) {
        String token = usersService.login(usersDto.getEmail(), usersDto.getPassword());
        if (token != null) {
            UsersDto user = usersService.getUserByEmail(usersDto.getEmail());
            LoginResponseDto response = new LoginResponseDto(
                    user.getId(),
                    user.getName(),
                    user.getRole(),
                    token
            );
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Create uploads directory if it doesn't exist
        Path uploadsDir = Paths.get("uploads");
        if (!Files.exists(uploadsDir)) {
            Files.createDirectories(uploadsDir);
        }

        Path path = Paths.get("uploads/" + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return ResponseEntity.ok("/api/users/images/" + fileName);
    }



    @PostMapping(value = "/register-with-pic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsersDto> registerUserWithProfilePic(
            @RequestPart("user") UsersDto usersDto,
            @RequestPart(value = "profilePic", required = false) MultipartFile profilePic
    ) throws IOException {
        if (profilePic != null && !profilePic.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + profilePic.getOriginalFilename();

            Path uploadsDir = Paths.get("uploads");
            if (!Files.exists(uploadsDir)) {
                Files.createDirectories(uploadsDir);
            }

            Path path = Paths.get("uploads/" + fileName);
            Files.copy(profilePic.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            usersDto.setProfilePic("/api/users/images/" + fileName);
        }

        UsersDto registeredUser = usersService.registerUser(usersDto);
        return ResponseEntity.ok(registeredUser);
    }




    /**
     * Sends the actual profile picture file for a given user id (not just the stored URL/path).
     * JWT must belong to the same user.
     */
    @GetMapping(value = "/{id}/profile-pic/file")
    public ResponseEntity<Resource> getProfilePicFileByUserId(
            @RequestHeader("Authorization") String token,
            @PathVariable int id
    ) throws IOException {
        if (!verifyTokenOwnedByUser(token, id)) {
            return ResponseEntity.status(403).build();
        }

        UsersDto user = usersService.getUserById(id);
        if (user == null || user.getProfilePic() == null || user.getProfilePic().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        // Expected stored values: "/api/users/images/<filename>" (or just the filename).
        String stored = user.getProfilePic().trim();
        String filename = stored;
        int idx = stored.lastIndexOf('/');
        if (idx >= 0 && idx < stored.length() - 1) {
            filename = stored.substring(idx + 1);
        }

        Path path = Paths.get("uploads").resolve(filename).normalize();
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(path.toUri());
        String contentType = Files.probeContentType(path);
        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName().toString() + "\"")
                .body(resource);
    }

    /**
     * Check whether the given user is verified.
     * Authorization: JWT must belong to the same user id.
     */
    @GetMapping("/{id}/verified")
    public ResponseEntity<Boolean> isUserVerified(
            @RequestHeader("Authorization") String token,
            @PathVariable int id
    ) {
        if (!verifyTokenOwnedByUser(token, id)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(usersService.isEmailVerified(id));
    }

    /**
     * Google sign-in/sign-up (token flow).
     * Frontend sends a Google ID token (JWT).
     * If the user doesn't exist yet, role can be provided; if null/blank, default to JobSeeker.
     */
    @PostMapping("/oauth2/google")
    public ResponseEntity<LoginResponseDto> googleOauth(@RequestBody GoogleAuthRequestDto body) {
        if (body == null || body.getIdToken() == null || body.getIdToken().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        LoginResponseDto response = usersService.loginOrSignupWithGoogleIdToken(body.getIdToken(), body.getRole());
        return ResponseEntity.ok(response);
    }

}
