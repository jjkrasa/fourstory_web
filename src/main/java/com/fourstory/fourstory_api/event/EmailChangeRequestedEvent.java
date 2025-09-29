package com.fourstory.fourstory_api.event;

public record EmailChangeRequestedEvent(
        String newEmail,
        String rawToken
) {
}
