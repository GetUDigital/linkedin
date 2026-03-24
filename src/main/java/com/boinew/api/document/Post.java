package com.boinew.api.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stored in MongoDB because posts have flexible media, rich reaction maps,
 * and high write volume — all better suited to a document store.
 */
@Document(collection = "posts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Post {

    @Id
    private String postId;

    @Indexed
    private Long userId;            // FK reference to MySQL users.user_id

    private String content;
    private String mediaUrl;
    private MediaType mediaType;
    private Visibility visibility;

    // reaction_type -> list of userIds who reacted
    @Builder.Default
    private Map<String, List<Long>> reactions = new HashMap<>();

    @Builder.Default
    private int commentCount = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum MediaType { image, video, document, none }
    public enum Visibility { PUBLIC, CONNECTIONS, ONLY_ME }
}
