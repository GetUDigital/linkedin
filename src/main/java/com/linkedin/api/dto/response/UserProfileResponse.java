package com.linkedin.api.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String headline;
    private String summary;
    private String profilePhoto;
    private String coverPhoto;
    private String location;
    private String website;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    private List<ExperienceResponse> experiences;
    private List<EducationResponse> educations;
    private List<String> skills;
    private int connectionCount;
    private String connectionStatus; // none | pending_sent | pending_received | connected

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceResponse {
        private Long expId;
        private String title;
        private String companyName;
        private String employmentType;
        private String location;
        private String startDate;
        private String endDate;
        private Boolean isCurrent;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationResponse {
        private Long eduId;
        private String institution;
        private String degree;
        private String fieldOfStudy;
        private Integer startYear;
        private Integer endYear;
        private String grade;
    }
}
