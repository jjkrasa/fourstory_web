package com.fourstory.fourstory_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PasswordChangeRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Pattern(
            regexp = "^(?!\\s)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=\\[\\]{};':\"\\\\|,.<>/?`~])[ -~]{6,64}(?<!\\s)$",
            message = "Password must be between 6 and 64 character long, include at least one lowercase letter, one uppercase letter, one digit, and one special character, and must not start or end with a space."
    )
    @NotBlank(message = "New password is required")
    private String newPassword;
}
