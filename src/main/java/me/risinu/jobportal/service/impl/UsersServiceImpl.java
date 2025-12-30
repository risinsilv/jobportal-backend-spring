package me.risinu.jobportal.service.impl;

import me.risinu.jobportal.dto.GoogleUserInfoDto;
import me.risinu.jobportal.dto.LoginResponseDto;
import me.risinu.jobportal.dto.UsersDto;
import me.risinu.jobportal.entity.Users;
import me.risinu.jobportal.repo.UsersRepo;
import me.risinu.jobportal.service.GoogleOAuthService;
import me.risinu.jobportal.service.UsersService;
import me.risinu.jobportal.util.JWT;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JWT tokenGenerator;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GoogleOAuthService googleOAuthService;

    @Override
    public List<UsersDto> getAllUsers() {
        return usersRepo.findAll().stream()
                .map(user -> modelMapper.map(user, UsersDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UsersDto getUserById(int id) {
        return usersRepo.findById(id)
                .map(user -> modelMapper.map(user, UsersDto.class))
                .orElse(null);
    }

    @Override
    public UsersDto createUser(UsersDto usersDto) {
        Users user = modelMapper.map(usersDto, Users.class);
        Users savedUser = usersRepo.save(user);
        return modelMapper.map(savedUser, UsersDto.class);
    }

    @Override
    public UsersDto updateUser(int id, UsersDto usersDto) {
        Users existingUser = usersRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (usersDto.getName() != null) {
            existingUser.setName(usersDto.getName());
        }
        if (usersDto.getProfilePic() != null) {
            existingUser.setProfilePic(usersDto.getProfilePic());
        }

        Users savedUser = usersRepo.save(existingUser);
        return modelMapper.map(savedUser, UsersDto.class);
    }

    @Override
    public void deleteUser(int id) {
        usersRepo.deleteById(id);
    }

    @Override
    public UsersDto registerUser(UsersDto usersDto) {
        String encryptedPassword = Base64.getEncoder().encodeToString(usersDto.getPassword().getBytes());
        usersDto.setPassword(encryptedPassword);
        Users user = modelMapper.map(usersDto, Users.class);

        // Always start unverified; only OTP verification can flip it.
        user.setVerified(false);
        if (user.getAuthProvider() == null) {
            user.setAuthProvider(Users.AuthProvider.LOCAL);
        }

        Users savedUser = usersRepo.save(user);
        return modelMapper.map(savedUser, UsersDto.class);
    }

    @Override
    public String login(String email, String password) {
        Users user = usersRepo.findByEmail(email).orElse(null);
        if (user != null) {
            String encryptedPassword = Base64.getEncoder().encodeToString(password.getBytes());
            if (user.getPassword() != null && user.getPassword().equals(encryptedPassword)) {
                UsersDto dto = modelMapper.map(user, UsersDto.class);
                return tokenGenerator.generateToken(dto);
            }
        }
        return null;
    }

    @Override
    public UsersDto getUserByEmail(String email) {
        return usersRepo.findByEmail(email)
                .map(user -> modelMapper.map(user, UsersDto.class))
                .orElse(null);
    }

    @Override
    public boolean isEmailVerified(int userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.isVerified();
    }

    @Override
    public void markEmailVerified(int userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isVerified()) {
            user.setVerified(true);
            usersRepo.save(user);
        }
    }

    @Override
    public String getProfilePicByEmail(String email) {
        return usersRepo.findByEmail(email)
                .map(Users::getProfilePic)
                .orElse(null);
    }

    private String downloadGoogleProfilePicToUploads(String pictureUrl) {
        if (pictureUrl == null || pictureUrl.isBlank()) return null;

        try {
            Path uploadsDir = Paths.get("uploads");
            if (!Files.exists(uploadsDir)) {
                Files.createDirectories(uploadsDir);
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(pictureUrl)).GET().build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return null;
            }

            String contentType = response.headers().firstValue("content-type").orElse(null);
            String ext = ".img";
            if (contentType != null) {
                String ct = contentType.toLowerCase();
                if (ct.contains("png")) ext = ".png";
                else if (ct.contains("jpeg") || ct.contains("jpg")) ext = ".jpg";
                else if (ct.contains("webp")) ext = ".webp";
            }

            String fileName = UUID.randomUUID() + "_google" + ext;
            Path filePath = uploadsDir.resolve(fileName).normalize();
            Files.write(filePath, response.body());

            // Store only local endpoint path in DB
            return "/api/users/images/" + fileName;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public LoginResponseDto loginOrSignupWithGoogleIdToken(String idToken, String requestedRole) {
        GoogleUserInfoDto googleUser = googleOAuthService.validateIdTokenAndGetUser(idToken);

        // Try to find existing user first (googleId), then fallback to email.
        Optional<Users> byGoogleId = (googleUser.getSub() == null) ? Optional.empty() : usersRepo.findByGoogleId(googleUser.getSub());
        Users user = byGoogleId.orElseGet(() -> usersRepo.findByEmail(googleUser.getEmail()).orElse(null));

        boolean isNewUser = (user == null);
        if (isNewUser) {
            user = new Users();
            user.setName(googleUser.getName());
            user.setEmail(googleUser.getEmail());
            user.setGoogleId(googleUser.getSub());
            user.setAuthProvider(Users.AuthProvider.GOOGLE);
            user.setPassword(null);

            // role: only matters on first-time sign-up
            String role = (requestedRole == null || requestedRole.isBlank()) ? "JobSeeker" : requestedRole;
            try {
                user.setRole(Users.Role.valueOf(role));
            } catch (IllegalArgumentException ex) {
                user.setRole(Users.Role.JobSeeker);
            }

            // profile pic: download to uploads and store local path; fallback to URL if download fails
            String localPicPath = downloadGoogleProfilePicToUploads(googleUser.getPicture());
            user.setProfilePic(localPicPath != null ? localPicPath : googleUser.getPicture());

            // With Google, if email is verified, mark as verified.
            boolean verified = Boolean.TRUE.equals(googleUser.getEmailVerified());
            user.setVerified(verified);

            user = usersRepo.save(user);
        } else {
            // Existing user: link googleId if missing
            if (user.getGoogleId() == null && googleUser.getSub() != null) {
                user.setGoogleId(googleUser.getSub());
            }
            if (user.getAuthProvider() == null) {
                user.setAuthProvider(Users.AuthProvider.LOCAL);
            }
            // If user signs in with Google and Google says email verified, upgrade verification.
            if (!user.isVerified() && Boolean.TRUE.equals(googleUser.getEmailVerified())) {
                user.setVerified(true);
            }
            user = usersRepo.save(user);
        }

        UsersDto dto = modelMapper.map(user, UsersDto.class);
        String jwt = tokenGenerator.generateToken(dto);

        return new LoginResponseDto(user.getId(), user.getName(), String.valueOf(user.getRole()), jwt);
    }
}
