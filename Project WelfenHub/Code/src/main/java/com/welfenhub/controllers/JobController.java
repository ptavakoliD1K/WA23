package com.welfenhub.controllers;

import com.welfenhub.dto.JobDTO;
import com.welfenhub.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    JobService jobService;

    @PostMapping("/post")
    public void postJob(@RequestBody JobDTO jobDTO) {
        jobService.saveJob(jobDTO.getTitle(), jobDTO.getContent(), jobDTO.getUrl());
    }

    @GetMapping("/get")
    public List<JobDTO> getJobs() {
        return jobService.getAllJobs();
    }

}