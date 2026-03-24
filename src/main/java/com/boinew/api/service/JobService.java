package com.boinew.api.service;

import com.boinew.api.entity.Job;
import com.boinew.api.entity.JobApplication;
import com.boinew.api.entity.User;
import com.boinew.api.exception.BadRequestException;
import com.boinew.api.exception.ConflictException;
import com.boinew.api.exception.ResourceNotFoundException;
import com.boinew.api.repository.mysql.JobApplicationRepository;
import com.boinew.api.repository.mysql.JobRepository;
import com.boinew.api.repository.mysql.UserRepository;
import com.boinew.api.entity.*;
import com.boinew.api.exception.*;
import com.boinew.api.repository.mysql.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<Job> getActiveJobs(int page, int size) {
        return jobRepository.findByIsActiveTrue(PageRequest.of(page, size, Sort.by("postedAt").descending()));
    }

    @Transactional(readOnly = true)
    public Page<Job> searchJobs(String query, int page, int size) {
        return jobRepository.searchActiveJobs(query,
                PageRequest.of(page, size, Sort.by("postedAt").descending()));
    }

    @Transactional(readOnly = true)
    public Job getJob(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));
    }

    @Transactional
    public JobApplication applyToJob(Long jobId, Long userId) {
        if (applicationRepository.existsByJob_JobIdAndUser_UserId(jobId, userId)) {
            throw new ConflictException("You have already applied to this job");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        if (!job.getIsActive()) {
            throw new BadRequestException("This job posting is no longer active");
        }

        User user = userRepository.getReferenceById(userId);

        JobApplication application = JobApplication.builder()
                .job(job)
                .user(user)
                .status(JobApplication.ApplicationStatus.applied)
                .build();

        return applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public java.util.List<JobApplication> getMyApplications(Long userId) {
        return applicationRepository.findByUser_UserId(userId);
    }
}
