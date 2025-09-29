package com.fourstory.fourstory_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenRequest {

    @NotBlank(message = "Token is required")
    private String token;
}
