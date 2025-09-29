package com.fourstory.fourstory_api.event;

public record VerificationResentEvent(
        String email,
        String rawToken
) {
}
