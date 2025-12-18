package me.risinu.jobportal.service.impl;

import jakarta.annotation.PostConstruct;
import me.risinu.jobportal.dto.JobSeekersDto;
import me.risinu.jobportal.entity.JobSeekers;
import me.risinu.jobportal.entity.Users;
import me.risinu.jobportal.repo.JobSeekersRepo;
import me.risinu.jobportal.repo.UsersRepo;
import me.risinu.jobportal.service.JobSeekersService;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobSeekersServiceImpl implements JobSeekersService {

    @Autowired
    private JobSeekersRepo jobSeekersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UsersRepo usersRepository;

    @Override
    public JobSeekersDto createJobSeeker(JobSeekersDto jobSeekersDto) {
        Users user = usersRepository.findById(jobSeekersDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent duplicate JobSeekers for the same user
        if (jobSeekersRepository.existsById(user.getId())) {
            throw new RuntimeException("JobSeeker already exists for this user");
        }

        JobSeekers jobSeekers = modelMapper.map(jobSeekersDto, JobSeekers.class);
        jobSeekers.setUser(user);

        JobSeekers savedJobSeeker = jobSeekersRepository.save(jobSeekers);
        return modelMapper.map(savedJobSeeker, JobSeekersDto.class);
    }

    @Override
    public JobSeekersDto getJobSeekerById(int id) {
        JobSeekers jobSeekers = jobSeekersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobSeeker not found"));
        return modelMapper.map(jobSeekers, JobSeekersDto.class);
    }

    @Override
    public JobSeekersDto updateJobSeeker(int id, JobSeekersDto jobSeekersDto) {
        JobSeekers existingJobSeeker = jobSeekersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobSeeker not found"));

        // Manually update fields (do not update id or user)
        existingJobSeeker.setTitle(jobSeekersDto.getTitle());
        existingJobSeeker.setAddress(jobSeekersDto.getAddress());
        existingJobSeeker.setResumeUrl(jobSeekersDto.getResumeUrl());
        existingJobSeeker.setProfileSummary(jobSeekersDto.getProfileSummary());
        existingJobSeeker.setSkills(jobSeekersDto.getSkills());
        existingJobSeeker.setJobHistory(jobSeekersDto.getJobHistory());
        existingJobSeeker.setExperience(jobSeekersDto.getExperience());
        existingJobSeeker.setCertifications(jobSeekersDto.getCertifications());
        existingJobSeeker.setContactInfo(jobSeekersDto.getContactInfo());
        existingJobSeeker.setEducation(jobSeekersDto.getEducation());
        // Do not set id or user

        JobSeekers updatedJobSeeker = jobSeekersRepository.save(existingJobSeeker);
        return modelMapper.map(updatedJobSeeker, JobSeekersDto.class);
    }

    @Override
    public void deleteJobSeeker(int id) {
        if (!jobSeekersRepository.existsById(id)) {
            throw new RuntimeException("JobSeeker not found");
        }
        jobSeekersRepository.deleteById(id);
    }

    @Override
    public List<JobSeekersDto> getAllJobSeekers() {
        return jobSeekersRepository.findAll().stream()
                .map(jobSeeker -> modelMapper.map(jobSeeker, JobSeekersDto.class))
                .collect(Collectors.toList());
    }
    @Override
    public void uploadCv(int id, MultipartFile cvFile) {
        JobSeekers jobSeeker = jobSeekersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("JobSeeker not found"));
        try {
            Path uploadsDir = Paths.get("userResume");
            if (!Files.exists(uploadsDir)) {
                Files.createDirectories(uploadsDir);
            }
            String fileName = "cv_" + id + ".pdf";
            Path path = Paths.get("userResume/" + fileName);
            Files.copy(cvFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store CV", e);
        }
    }
    @Override
    public byte[] getCv(int userId) {
        String filePath = "userResume/cv_" + userId + ".pdf";
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try {
            return java.nio.file.Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CV", e);
        }
    }

    @Override
    public void deleteCv(int userId) {
        JobSeekers jobSeeker = jobSeekersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("JobSeeker not found"));
        String filePath = "userResume/cv_" + userId + ".pdf";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        jobSeeker.setResumeUrl(null);
        jobSeekersRepository.save(jobSeeker);
    }


}
