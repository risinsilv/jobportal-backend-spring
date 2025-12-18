package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.LoginResponseDto;
import me.risinu.jobportal.dto.UsersDto;
import me.risinu.jobportal.service.UsersService;
import me.risinu.jobportal.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.StandardCopyOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;

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

    private boolean verifyToken(String token) {
        return tokenGenerator.verifyToken(token);
    }

    @GetMapping
    public ResponseEntity<List<UsersDto>> getAllUsers(@RequestHeader("Authorization") String token) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        List<UsersDto> users = usersService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersDto> getUserById(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        UsersDto user = usersService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UsersDto> createUser(@RequestHeader("Authorization") String token, @RequestBody UsersDto usersDto) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        UsersDto createdUser = usersService.createUser(usersDto);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsersDto> updateUser(@RequestHeader("Authorization") String token, @PathVariable int id, @RequestBody UsersDto usersDto) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        UsersDto updatedUser = usersService.updateUser(id, usersDto);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
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
        return ResponseEntity.ok("/api/images/" + fileName);
    }

    // Serve image endpoint
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        Path path = Paths.get("uploads/" + filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .header("Content-Type", Files.probeContentType(path))
                .body(resource);
    }



}
