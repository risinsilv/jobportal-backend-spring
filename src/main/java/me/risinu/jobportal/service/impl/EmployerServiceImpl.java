package me.risinu.jobportal.service.impl;


import me.risinu.jobportal.dto.EmployerDto;
import me.risinu.jobportal.entity.Employer;
import me.risinu.jobportal.entity.Users;
import me.risinu.jobportal.repo.EmployerRepo;
import me.risinu.jobportal.repo.UsersRepo;
import me.risinu.jobportal.service.EmployerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployerServiceImpl implements EmployerService {

    @Autowired
    private EmployerRepo employerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UsersRepo usersRepository;


    @Override
    public EmployerDto createEmployer(EmployerDto employerDto) {
        Users user = usersRepository.findById(employerDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Employer employer = modelMapper.map(employerDto, Employer.class);
        employer.setUser(user); // Set the user entity
        Employer saved = employerRepository.save(employer);
        return modelMapper.map(saved, EmployerDto.class);
    }

    @Override
    public EmployerDto getEmployerById(int id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        return modelMapper.map(employer, EmployerDto.class);
    }

    @Override
    public EmployerDto updateEmployer(int id, EmployerDto employerDto) {
        Employer existingEmployer = employerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        // Manually update only allowed fields
        existingEmployer.setCompanyName(employerDto.getCompanyName());
        existingEmployer.setCompanyWebsite(employerDto.getCompanyWebsite());
        existingEmployer.setCompanyAddress(employerDto.getCompanyAddress());
        existingEmployer.setContactInfo(employerDto.getContactInfo());
        existingEmployer.setPosition(employerDto.getPosition());

        Employer updatedEmployer = employerRepository.save(existingEmployer);
        return modelMapper.map(updatedEmployer, EmployerDto.class);
    }

    @Override
    public void deleteEmployer(int id) {
        if (!employerRepository.existsById(id)) {
            throw new RuntimeException("Employer not found");
        }
        employerRepository.deleteById(id);
    }
}
