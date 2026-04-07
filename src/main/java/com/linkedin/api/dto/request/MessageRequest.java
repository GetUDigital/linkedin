package com.linkedin.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class MessageRequest {

    @Data
    public static class Send {
        @NotNull
        private Long recipientId;

        @NotBlank
        private String content;
    }

    @Data
    public static class Reply {
        @NotBlank
        private String content;
    }
}
