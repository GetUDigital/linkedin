package com.linkedin.api.repository.mysql;

import com.linkedin.api.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByUser_UserId(Long userId);

    List<JobApplication> findByJob_JobId(Long jobId);

    Optional<JobApplication> findByJob_JobIdAndUser_UserId(Long jobId, Long userId);

    boolean existsByJob_JobIdAndUser_UserId(Long jobId, Long userId);
}
