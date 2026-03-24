package com.boinew.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

public class AuthRequest {

    @Data
    public static class Register {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;

        @NotBlank
        private String firstName;

        @NotBlank
        private String lastName;
    }

    @Data
    public static class Login {
        @Email @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    public static class RefreshToken {
        @NotBlank
        private String refreshToken;
    }
}
