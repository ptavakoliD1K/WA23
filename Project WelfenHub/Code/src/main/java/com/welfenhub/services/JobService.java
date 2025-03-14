package com.welfenhub.services;

import com.welfenhub.dto.JobDTO;
import com.welfenhub.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {

    @Autowired
    JobRepository jobRepository;

    public void saveJob(String title, String content, String url) {
        LocalDateTime creationDate = LocalDateTime.now();

        jobRepository.saveJob(title, content, url, creationDate.toString());
    }

    public List<JobDTO> getAllJobs () {
        return jobRepository.getJobs();
    }

    public  void removeJobs(String title, String content) {
        jobRepository.removeJob(title, content);
    }
}
