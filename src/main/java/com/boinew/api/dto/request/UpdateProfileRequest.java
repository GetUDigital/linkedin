package com.boinew.api.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Size(max = 220)
    private String headline;

    private String summary;

    @Size(max = 100)
    private String location;

    @Size(max = 255)
    private String website;

    private String profilePhoto;
    private String coverPhoto;
}
