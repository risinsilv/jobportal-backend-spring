package me.risinu.jobportal.service;


import me.risinu.jobportal.dto.EmployerDto;

public interface EmployerService {
    EmployerDto createEmployer(EmployerDto employerDto);
    EmployerDto getEmployerById(int id);
    EmployerDto updateEmployer(int id, EmployerDto employerDto);
    void deleteEmployer(int id);
}
