package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.event.EmailChangeRequestedEvent;
import com.fourstory.fourstory_api.event.PasswordChangedEvent;
import com.fourstory.fourstory_api.event.PasswordResetRequestedEvent;
import com.fourstory.fourstory_api.event.VerificationResentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailDispatcher {

    private final EmailSender emailSender;

    @Value("${frontend.url}")
    private String url;

    public void sendVerifyEmail(VerificationResentEvent event) {
        String link = url + "/verify?token=" + event.rawToken();
        String body = "<p>Click to verify: <a href=\"" + link + "\">Verify</a></p>";

        emailSender.send(event.email(), "Verify your email", body);
    }

    public void sendForgotPassword(PasswordResetRequestedEvent event) {
        String link = url + "/password-reset?token=" + event.rawToken();
        String body = "<p><a href=\"" + link + "\">Reset password</a></p>";

        emailSender.send(event.email(), "Reset your password", body);
    }

    public void sendChangeEmail(EmailChangeRequestedEvent event) {
        String link = url + "/email-change/confirm?token=" + event.rawToken();
        String body = "<p><a href=\"" + link + "\">Confirm email</a></p>";

        emailSender.send(event.newEmail(), "Confirm your new email", body);
    }

    public void sendPasswordChangedNotification(PasswordChangedEvent event) {
        String body = "<p>Your password has been changed successfully. If you did not perform this action, please contact support immediately.</p>";

        emailSender.send(event.email(), "Your password has been changed", body);
    }
}
