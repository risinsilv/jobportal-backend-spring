package me.risinu.jobportal.service;

import me.risinu.jobportal.dto.UsersDto;

import java.util.List;

public interface UsersService {
    List<UsersDto> getAllUsers();
    UsersDto getUserById(int id);
    UsersDto createUser(UsersDto usersDto);
    UsersDto updateUser(int id, UsersDto usersDto);
    void deleteUser(int id);
    UsersDto registerUser(UsersDto usersDto);
    String login(String email, String password);
}
