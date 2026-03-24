package com.boinew.api.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Threaded comments stored in MongoDB with postId index for fast retrieval.
 * parentId supports nested replies (one level deep mirrors LinkedIn behaviour).
 */
@Document(collection = "comments")
@CompoundIndex(name = "post_created_idx", def = "{'postId': 1, 'createdAt': -1}")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Comment {

    @Id
    private String commentId;

    @Indexed
    private String postId;

    private Long userId;

    private String parentId;   // null = top-level comment

    private String content;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
