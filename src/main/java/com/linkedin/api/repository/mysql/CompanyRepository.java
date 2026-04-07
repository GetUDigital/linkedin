package com.linkedin.api.repository.mysql;

import com.linkedin.api.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByIndustryIgnoreCase(String industry);
    List<Company> findByNameContainingIgnoreCase(String name);
}
