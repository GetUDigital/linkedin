package com.linkedin.api.service;

import com.linkedin.api.dto.request.UpdateProfileRequest;
import com.linkedin.api.dto.response.UserProfileResponse;
import com.linkedin.api.entity.Connection;
import com.linkedin.api.entity.Skill;
import com.linkedin.api.entity.User;
import com.linkedin.api.repository.mysql.ConnectionRepository;
import com.linkedin.api.repository.mysql.UserRepository;
import com.linkedin.api.entity.*;
import com.linkedin.api.exception.ResourceNotFoundException;
import com.linkedin.api.repository.mysql.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long profileUserId, Long currentUserId) {
        User user = userRepository.findById(profileUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", profileUserId));

        String connectionStatus = resolveConnectionStatus(currentUserId, profileUserId);

        long connectionCount = connectionRepository
                .findByUserAndStatus(profileUserId, Connection.ConnectionStatus.accepted)
                .size();

        return buildProfileResponse(user, connectionStatus, (int) connectionCount);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (req.getFirstName()   != null) user.setFirstName(req.getFirstName());
        if (req.getLastName()    != null) user.setLastName(req.getLastName());
        if (req.getHeadline()    != null) user.setHeadline(req.getHeadline());
        if (req.getSummary()     != null) user.setSummary(req.getSummary());
        if (req.getLocation()    != null) user.setLocation(req.getLocation());
        if (req.getWebsite()     != null) user.setWebsite(req.getWebsite());
        if (req.getProfilePhoto()!= null) user.setProfilePhoto(req.getProfilePhoto());
        if (req.getCoverPhoto()  != null) user.setCoverPhoto(req.getCoverPhoto());

        userRepository.save(user);
        return buildProfileResponse(user, "self", 0);
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> searchUsers(String query) {
        return userRepository.searchUsers(query).stream()
                .map(u -> buildProfileResponse(u, "none", 0))
                .collect(Collectors.toList());
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private String resolveConnectionStatus(Long currentUserId, Long profileUserId) {
        if (currentUserId.equals(profileUserId)) return "self";

        return connectionRepository
                .findBySender_UserIdAndReceiver_UserId(currentUserId, profileUserId)
                .map(c -> switch (c.getStatus()) {
                    case accepted -> "connected";
                    case pending  -> "pending_sent";
                    default       -> "none";
                })
                .orElseGet(() ->
                    connectionRepository
                        .findBySender_UserIdAndReceiver_UserId(profileUserId, currentUserId)
                        .map(c -> c.getStatus() == Connection.ConnectionStatus.pending
                                  ? "pending_received" : "none")
                        .orElse("none")
                );
    }

    public UserProfileResponse buildProfileResponse(User user, String connectionStatus, int connectionCount) {
        List<UserProfileResponse.ExperienceResponse> exps = user.getExperiences().stream()
                .map(e -> UserProfileResponse.ExperienceResponse.builder()
                        .expId(e.getExpId())
                        .title(e.getTitle())
                        .companyName(e.getCompanyName() != null ? e.getCompanyName()
                                : (e.getCompany() != null ? e.getCompany().getName() : null))
                        .employmentType(e.getEmploymentType() != null ? e.getEmploymentType().name() : null)
                        .location(e.getLocation())
                        .startDate(e.getStartDate() != null ? e.getStartDate().toString() : null)
                        .endDate(e.getEndDate() != null ? e.getEndDate().toString() : null)
                        .isCurrent(e.getIsCurrent())
                        .description(e.getDescription())
                        .build())
                .collect(Collectors.toList());

        List<UserProfileResponse.EducationResponse> edus = user.getEducations().stream()
                .map(e -> UserProfileResponse.EducationResponse.builder()
                        .eduId(e.getEduId())
                        .institution(e.getInstitution())
                        .degree(e.getDegree())
                        .fieldOfStudy(e.getFieldOfStudy())
                        .startYear(e.getStartYear())
                        .endYear(e.getEndYear())
                        .grade(e.getGrade())
                        .build())
                .collect(Collectors.toList());

        List<String> skills = user.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toList());

        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .headline(user.getHeadline())
                .summary(user.getSummary())
                .profilePhoto(user.getProfilePhoto())
                .coverPhoto(user.getCoverPhoto())
                .location(user.getLocation())
                .website(user.getWebsite())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .experiences(exps)
                .educations(edus)
                .skills(skills)
                .connectionCount(connectionCount)
                .connectionStatus(connectionStatus)
                .build();
    }
}
