package com.linkedin.api.controller;

import com.linkedin.api.config.CurrentUser;
import com.linkedin.api.dto.request.UpdateProfileRequest;
import com.linkedin.api.dto.response.UserProfileResponse;
import com.linkedin.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final CurrentUser currentUser;

    @Operation(summary = "Get your own profile")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        Long userId = currentUser.getId();
        return ResponseEntity.ok(userService.getProfile(userId, userId));
    }

    @Operation(summary = "Get a user's profile by ID")
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId, currentUser.getId()));
    }

    @Operation(summary = "Update your profile")
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(userService.updateProfile(currentUser.getId(), req));
    }

    @Operation(summary = "Search users by name or headline")
    @GetMapping("/search")
    public ResponseEntity<List<UserProfileResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(userService.searchUsers(q));
    }
}
