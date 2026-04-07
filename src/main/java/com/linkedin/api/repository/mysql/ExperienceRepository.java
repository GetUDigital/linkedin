package com.linkedin.api.repository.mysql;

import com.linkedin.api.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByUser_UserIdOrderByStartDateDesc(Long userId);
}
