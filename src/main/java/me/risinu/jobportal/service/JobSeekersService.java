package me.risinu.jobportal.service;
import me.risinu.jobportal.dto.JobSeekersDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobSeekersService {
    JobSeekersDto createJobSeeker(JobSeekersDto jobSeekersDto);
    JobSeekersDto getJobSeekerById(int id);
    JobSeekersDto updateJobSeeker(int id, JobSeekersDto jobSeekersDto);
    List<JobSeekersDto> getAllJobSeekers();
    void deleteJobSeeker(int id);
    void uploadCv(int id, MultipartFile cvFile);
    byte []getCv(int id);
    void deleteCv(int id);
}