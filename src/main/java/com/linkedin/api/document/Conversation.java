package com.linkedin.api.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Conversations stored in MongoDB — schemaless participant lists scale
 * naturally and message history avoids MySQL join overhead.
 */
@Document(collection = "conversations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Conversation {

    @Id
    private String convoId;

    @Builder.Default
    private List<Long> memberIds = new ArrayList<>();   // MySQL user IDs

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime lastMessageAt = LocalDateTime.now();
}
