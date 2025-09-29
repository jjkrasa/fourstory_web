package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.event.PasswordResetRequestedEvent;
import com.fourstory.fourstory_api.exception.BusinessException;
import com.fourstory.fourstory_api.exception.ErrorCode;
import com.fourstory.fourstory_api.model.tglobal.OutboxEventType;
import com.fourstory.fourstory_api.model.tglobal.User;
import com.fourstory.fourstory_api.model.tglobal.VerificationToken;
import com.fourstory.fourstory_api.model.tglobal.VerificationTokenType;
import com.fourstory.fourstory_api.repository.tglobal.UserRepository;
import com.fourstory.fourstory_api.repository.tglobal.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OutboxService outboxService;

    @Mock
    private VerificationTokenService verificationTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .email("email@email.com")
                .password("Password")
                .build();
    }

    @Nested
    class RequestForgotPassword {

        @Test
        void requestForgotPassword_shouldNotSendEmail_whenUserNotFound() {
            when(userRepository.findByEmailIgnoreCase("email@email.com")).thenReturn(Optional.empty());

            passwordResetService.requestForgotPassword("email@email.com");

            verify(userRepository).findByEmailIgnoreCase("email@email.com");
        }

        @Test
        void requestForgotPassword_shouldSendEmail() {
            when(userRepository.findByEmailIgnoreCase("email@email.com")).thenReturn(Optional.of(user));
            when(verificationTokenService.createAndSaveToken(user, VerificationTokenType.PASSWORD_RESET, 1)).thenReturn("rawToken");

            passwordResetService.requestForgotPassword("email@email.com");

            verify(userRepository).findByEmailIgnoreCase("email@email.com");
            verify(verificationTokenRepository).invalidateActive(eq(user.getId()), eq(VerificationTokenType.PASSWORD_RESET), any(Instant.class));
            verify(verificationTokenService).createAndSaveToken(user, VerificationTokenType.PASSWORD_RESET, 1);
            verify(outboxService).enqueue(eq(OutboxEventType.PASSWORD_RESET_REQUEST), any(PasswordResetRequestedEvent.class));
        }
    }

    @Nested
    class ConfirmPasswordResetChange {

        @Test
        void confirmPasswordResetChange_shouldThrowException_whenNewPasswordIsSameAsOld() {
            VerificationToken verificationToken = VerificationToken.builder().user(user).consumedAt(null).build();
            when(verificationTokenService.getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.PASSWORD_RESET))
                    .thenReturn(verificationToken);
            when(passwordEncoder.matches("Password", user.getPassword())).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class, () ->
                    passwordResetService.confirmPasswordResetChange("rawToken", "Password")
            );

            assertEquals(ErrorCode.PASSWORD_SAME_AS_OLD, exception.getErrorCode());
            verify(verificationTokenService).getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.PASSWORD_RESET);
            verify(passwordEncoder).matches("Password", user.getPassword());
        }

        @Test
        void confirmPasswordResetChange_shouldChangePassword() {
            VerificationToken verificationToken = VerificationToken.builder().user(user).consumedAt(null).build();
            when(verificationTokenService.getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.PASSWORD_RESET))
                    .thenReturn(verificationToken);
            when(passwordEncoder.matches("NewPassword", user.getPassword())).thenReturn(false);
            when(passwordEncoder.encode("NewPassword")).thenReturn("EncodedNewPassword");

            passwordResetService.confirmPasswordResetChange("rawToken", "NewPassword");


            assertEquals("EncodedNewPassword", user.getPassword());
            assertNotNull(verificationToken.getConsumedAt());

            verify(verificationTokenService).getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.PASSWORD_RESET);
            verify(passwordEncoder).matches("NewPassword", "Password");
            verify(passwordEncoder).encode("NewPassword");
        }
    }
}