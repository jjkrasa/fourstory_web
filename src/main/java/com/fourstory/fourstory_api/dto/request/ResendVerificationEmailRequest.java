package com.fourstory.fourstory_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResendVerificationEmailRequest {

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;
}
