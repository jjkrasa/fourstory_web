package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.event.VerificationResentEvent;
import com.fourstory.fourstory_api.model.tglobal.OutboxEventType;
import com.fourstory.fourstory_api.model.tglobal.User;
import com.fourstory.fourstory_api.model.tglobal.VerificationToken;
import com.fourstory.fourstory_api.model.tglobal.VerificationTokenType;
import com.fourstory.fourstory_api.repository.tglobal.UserRepository;
import com.fourstory.fourstory_api.repository.tglobal.VerificationTokenRepository;
import com.fourstory.fourstory_api.utils.TokenUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @InjectMocks
    EmailVerificationService emailVerificationService;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenUtil tokenUtil;

    @Mock
    private OutboxService outboxService;

    @Mock
    private VerificationTokenService verificationTokenService;

    @Nested
    class VerifyEmail {

        @Test
        public void verifyEmail_shouldSetEmailVerifiedTrue_whenEmailIsNotVerified() {
            User user = User.builder().id(1).emailVerified(false).build();

            VerificationToken verificationToken = VerificationToken.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                    .user(user)
                    .tokenType(VerificationTokenType.EMAIL_VERIFY)
                    .tokenHash(new byte[]{1, 2, 3})
                    .expiresAt(Instant.now().plus(Duration.ofHours(1)))
                    .build();

            when(verificationTokenService.getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_VERIFY))
                    .thenReturn(verificationToken);

            emailVerificationService.verifyEmail("rawToken");

            assertTrue(user.getEmailVerified());
            assertNotNull(verificationToken.getConsumedAt());
        }

        @Test
        public void verifyEmail_shouldSetEmailVerifiedTrue_whenEmailIsVerified() {
            User user = User.builder().id(1).emailVerified(true).build();

            VerificationToken verificationToken = VerificationToken.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                    .user(user)
                    .tokenType(VerificationTokenType.EMAIL_VERIFY)
                    .tokenHash(new byte[]{1, 2, 3})
                    .expiresAt(Instant.now().plus(Duration.ofHours(1)))
                    .build();

            when(verificationTokenService.getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_VERIFY))
                    .thenReturn(verificationToken);

            emailVerificationService.verifyEmail("rawToken");

            assertTrue(user.getEmailVerified());
            assertNotNull(verificationToken.getConsumedAt());
        }
    }

    @Nested
    class ResendVerificationEmail {

        @Test
        public void resendVerificationEmail_shouldNotBeSent_whenUserNotFound() {
            when(userRepository.findByEmailIgnoreCase("email@email.com")).thenReturn(Optional.empty());

            emailVerificationService.resendVerificationEmail("email@email.com");

            verify(userRepository).findByEmailIgnoreCase("email@email.com");
            verifyNoMoreInteractions(verificationTokenRepository, tokenUtil, outboxService);
        }

        @Test
        public void resendVerificationEmail_shouldNotBeSent_whenEmailAlreadyVerified() {
            User user = User.builder().id(1).email("email@email.com").emailVerified(true).build();
            when(userRepository.findByEmailIgnoreCase("email@email.com")).thenReturn(Optional.of(user));

            emailVerificationService.resendVerificationEmail("email@email.com");

            verify(userRepository).findByEmailIgnoreCase("email@email.com");
            verifyNoMoreInteractions(verificationTokenRepository, tokenUtil, outboxService);
        }

        @Test
        public void resendVerificationEmail_shouldSendNewVerificationEmail() {
            User user = User.builder().id(1).email("email@email.com").emailVerified(false).build();
            when(userRepository.findByEmailIgnoreCase("email@email.com")).thenReturn(Optional.of(user));
            when(verificationTokenService.createAndSaveToken(user, VerificationTokenType.EMAIL_VERIFY, 24)).thenReturn("rawToken");

            emailVerificationService.resendVerificationEmail("email@email.com");

            verify(userRepository).findByEmailIgnoreCase("email@email.com");
            verify(verificationTokenRepository).invalidateActive(eq(user.getId()), eq(VerificationTokenType.EMAIL_VERIFY), any(Instant.class));
            verify(verificationTokenService).createAndSaveToken(user, VerificationTokenType.EMAIL_VERIFY, 24);
            verify(outboxService).enqueue(OutboxEventType.EMAIL_VERIFICATION_REQUEST, new VerificationResentEvent(user.getEmail(), "rawToken"));
        }
    }

}