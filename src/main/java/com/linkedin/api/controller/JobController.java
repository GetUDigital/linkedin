package com.linkedin.api.controller;

import com.linkedin.api.config.CurrentUser;
import com.linkedin.api.entity.Job;
import com.linkedin.api.entity.JobApplication;
import com.linkedin.api.entity.*;
import com.linkedin.api.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs")
@SecurityRequirement(name = "bearerAuth")
public class JobController {

    private final JobService jobService;
    private final CurrentUser currentUser;

    @Operation(summary = "Browse active job listings")
    @GetMapping
    public ResponseEntity<Page<Job>> getJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(jobService.getActiveJobs(page, size));
    }

    @Operation(summary = "Search job listings by title or location")
    @GetMapping("/search")
    public ResponseEntity<Page<Job>> searchJobs(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(jobService.searchJobs(q, page, size));
    }

    @Operation(summary = "Get details of a specific job")
    @GetMapping("/{jobId}")
    public ResponseEntity<Job> getJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJob(jobId));
    }

    @Operation(summary = "Apply to a job")
    @PostMapping("/{jobId}/apply")
    public ResponseEntity<JobApplication> apply(@PathVariable Long jobId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.applyToJob(jobId, currentUser.getId()));
    }

    @Operation(summary = "View your submitted applications")
    @GetMapping("/my-applications")
    public ResponseEntity<List<JobApplication>> myApplications() {
        return ResponseEntity.ok(jobService.getMyApplications(currentUser.getId()));
    }
}
