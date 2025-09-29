package com.fourstory.fourstory_api.event;

public record PasswordResetRequestedEvent(
        String email,
        String rawToken
) {
}
