package com.fourstory.fourstory_api.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultEmailSenderTest {

    @InjectMocks
    private DefaultEmailSender emailSender;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailSender, "from", "email@email.com");
    }

    @Test
    public void send_shouldSendEmail() {
        Session session = Session.getInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(session);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String to = "user@example.com";
        String subject = "Welcome!";
        String body = "Hello";

        emailSender.send(to, subject, body);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    public void send_shouldCatchException() {
        when(mailSender.createMimeMessage()).thenThrow(new IllegalStateException("Cannot create MimeMessage"));

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> emailSender.send(null, null, null));

        Assertions.assertEquals("Failed to send email", exception.getMessage());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}