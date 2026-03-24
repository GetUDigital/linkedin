package com.boinew.api.dto.request;

import com.boinew.api.document.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class PostRequest {

    @Data
    public static class Create {
        @NotBlank
        private String content;
        private String mediaUrl;
        private Post.MediaType mediaType = Post.MediaType.none;
        private Post.Visibility visibility = Post.Visibility.PUBLIC;
    }

    @Data
    public static class Update {
        @NotBlank
        private String content;
        private Post.Visibility visibility;
    }

    @Data
    public static class React {
        @NotBlank
        private String reactionType; // like, celebrate, support, love, insightful, funny
    }
}
