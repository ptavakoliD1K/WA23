package com.welfenhub.controllers;

import com.welfenhub.dto.JobDTO;
import com.welfenhub.services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * rest-controller for job page
 */

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    JobService jobService;

    /**
     * post job
     * @param jobDTO
     */

    @PostMapping("/post")
    public void postJob(@RequestBody JobDTO jobDTO) {
        jobService.saveJob(jobDTO.getTitle(), jobDTO.getContent(), jobDTO.getUrl(), jobDTO.getColor());
    }


    /**
     * get job
     * @return
     */

    @GetMapping("/get")
    public List<JobDTO> getJobs() {
        return jobService.getAllJobs();
    }

    /**
     * deletes job
     * @param jobDTO
     */

    @DeleteMapping("/delete")
    public void deleteJob(@RequestBody JobDTO jobDTO) {
        jobService.removeJobs(jobDTO.getTitle(), jobDTO.getContent());

    }

}