package com.linkedin.api.controller;

import com.linkedin.api.config.CurrentUser;
import com.linkedin.api.document.Notification;
import com.linkedin.api.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUser currentUser;

    @Operation(summary = "Get your notifications (paginated)")
    @GetMapping
    public ResponseEntity<Page<Notification>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                notificationService.getNotifications(currentUser.getId(), page, size));
    }

    @Operation(summary = "Get count of unread notifications")
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        return ResponseEntity.ok(
                Map.of("unread", notificationService.getUnreadCount(currentUser.getId())));
    }

    @Operation(summary = "Mark all notifications as read")
    @PatchMapping("/mark-all-read")
    public ResponseEntity<Void> markAllRead() {
        notificationService.markAllRead(currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all read notifications")
    @DeleteMapping("/read")
    public ResponseEntity<Void> clearRead() {
        notificationService.clearRead(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
