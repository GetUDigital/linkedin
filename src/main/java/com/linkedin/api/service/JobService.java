package com.linkedin.api.service;

import com.linkedin.api.entity.Job;
import com.linkedin.api.entity.JobApplication;
import com.linkedin.api.entity.User;
import com.linkedin.api.exception.BadRequestException;
import com.linkedin.api.exception.ConflictException;
import com.linkedin.api.exception.ResourceNotFoundException;
import com.linkedin.api.repository.mysql.JobApplicationRepository;
import com.linkedin.api.repository.mysql.JobRepository;
import com.linkedin.api.repository.mysql.UserRepository;
import com.linkedin.api.entity.*;
import com.linkedin.api.exception.*;
import com.linkedin.api.repository.mysql.*;
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
