package me.risinu.jobportal.service.impl;

import me.risinu.jobportal.dto.UsersDto;
import me.risinu.jobportal.entity.Users;
import me.risinu.jobportal.repo.UsersRepo;
import me.risinu.jobportal.service.UsersService;
import me.risinu.jobportal.util.JWT;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private  UsersRepo usersRepo;

    @Autowired
    private JWT tokenGenerator;

    @Autowired
    private ModelMapper modelMapper;

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

        // Only update fields if they are not null in the DTO
        if (usersDto.getName() != null) {
            existingUser.setName(usersDto.getName());
        }
        if (usersDto.getProfilePic() != null) {
            existingUser.setProfilePic(usersDto.getProfilePic());
        }
        // Repeat for other fields as needed
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
        Users savedUser = usersRepo.save(user);
        return modelMapper.map(savedUser, UsersDto.class);
    }
    @Override
    public String login(String email, String password) {
        Users user = usersRepo.findByEmail(email).orElse(null);
        if (user != null) {
            String encryptedPassword = Base64.getEncoder().encodeToString(password.getBytes());
            if (user.getPassword().equals(encryptedPassword)) {
                return tokenGenerator.generateToken(modelMapper.map(user, UsersDto.class));
            }
        }
        return null; // Return null or throw an exception for invalid credentials
    }

    @Override
    public UsersDto getUserByEmail(String email) {
        return usersRepo.findByEmail(email)
                .map(user -> modelMapper.map(user, UsersDto.class))
                .orElse(null);
    }
}
