package com.boinew.api.repository.mysql;

import com.boinew.api.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.isActive = true AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           " LOWER(j.location) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Job> searchActiveJobs(@Param("q") String query, Pageable pageable);

    Page<Job> findByCompany_CompanyIdAndIsActiveTrue(Long companyId, Pageable pageable);
}
