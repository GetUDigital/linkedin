package com.linkedin.api.dto.response;

import com.linkedin.api.document.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private String postId;
    private Long userId;
    private String authorName;
    private String authorHeadline;
    private String authorPhoto;
    private String content;
    private String mediaUrl;
    private Post.MediaType mediaType;
    private Post.Visibility visibility;
    private Map<String, Integer> reactionCounts;
    private String currentUserReaction;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
