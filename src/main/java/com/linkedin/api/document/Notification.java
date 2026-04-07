package com.linkedin.api.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Notifications stored in MongoDB — high write rate, flexible ref_id,
 * natural TTL (old notifications expire after 90 days).
 */
@Document(collection = "notifications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Notification {

    @Id
    private String notifId;

    @Indexed
    private Long userId;        // recipient (MySQL user ID)

    private Long actorId;       // who triggered it (MySQL user ID)

    private NotificationType type;

    private String refId;       // postId / commentId / connectionId etc.

    @Builder.Default
    private boolean isRead = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum NotificationType {
        CONNECTION_REQUEST, POST_LIKE, COMMENT, MENTION, JOB_ALERT
    }
}
