package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.event.EmailChangeRequestedEvent;
import com.fourstory.fourstory_api.event.PasswordChangedEvent;
import com.fourstory.fourstory_api.event.PasswordResetRequestedEvent;
import com.fourstory.fourstory_api.event.VerificationResentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EmailDispatcherTest {

    @InjectMocks
    private EmailDispatcher emailDispatcher;

    @Mock
    private EmailSender emailSender;

    private final String BASE_URL = "http://localhost:3000";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailDispatcher, "url", BASE_URL);
    }

    @Test
    public void sendVerifyEmail_shouldSendEmail() {
        VerificationResentEvent event = new VerificationResentEvent("email@email.com", "rawToken");

        String link = BASE_URL + "/verify?token=" + event.rawToken();
        String body = "<p>Click to verify: <a href=\"" + link + "\">Verify</a></p>";

        emailDispatcher.sendVerifyEmail(event);

        Mockito.verify(emailSender).send(event.email(), "Verify your email", body);
    }

    @Test
    public void sendForgotPassword_shouldSendEmail() {
        PasswordResetRequestedEvent event = new PasswordResetRequestedEvent("email@email.com", "rawToken");

        String link = BASE_URL + "/password-reset?token=" + event.rawToken();
        String body = "<p><a href=\"" + link + "\">Reset password</a></p>";

        emailDispatcher.sendForgotPassword(event);

        Mockito.verify(emailSender).send(event.email(), "Reset your password", body);
    }

    @Test
    public void sendChangeEmail_shouldSendEmail() {
        EmailChangeRequestedEvent event = new EmailChangeRequestedEvent("email@email.com", "rawToken");

        String link = BASE_URL + "/email-change/confirm?token=" + event.rawToken();
        String body = "<p><a href=\"" + link + "\">Confirm email</a></p>";

        emailDispatcher.sendChangeEmail(event);

        Mockito.verify(emailSender).send(event.newEmail(), "Confirm your new email", body);
    }

    @Test
    public void sendPasswordChangedNotification_shouldSendEmail() {
        PasswordChangedEvent event = new PasswordChangedEvent("email@email.com");

        String body = "<p>Your password has been changed successfully. If you did not perform this action, please contact support immediately.</p>";

        emailDispatcher.sendPasswordChangedNotification(event);

        Mockito.verify(emailSender).send(event.email(), "Your password has been changed", body);
    }
}