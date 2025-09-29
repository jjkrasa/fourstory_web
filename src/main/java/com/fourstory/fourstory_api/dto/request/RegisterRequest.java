package com.fourstory.fourstory_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterRequest {

    @Pattern(
            regexp = "^(?! )[ -~]{4,50}(?<! )$",
            message = "Username must be between 4 and 50 characters long and must not start or end with a space."
    )
    private String userName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(
            regexp = "^(?!\\s)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=\\[\\]{};':\"\\\\|,.<>/?`~])[ -~]{6,64}(?<!\\s)$",
            message = "Password must be between 6 and 64 character long, include at least one lowercase letter, one uppercase letter, one digit, and one special character, and must not start or end with a space."
    )
    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
