package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.event.EmailChangeRequestedEvent;
import com.fourstory.fourstory_api.exception.BusinessException;
import com.fourstory.fourstory_api.exception.ErrorCode;
import com.fourstory.fourstory_api.model.tglobal.OutboxEventType;
import com.fourstory.fourstory_api.model.tglobal.User;
import com.fourstory.fourstory_api.model.tglobal.VerificationToken;
import com.fourstory.fourstory_api.model.tglobal.VerificationTokenType;
import com.fourstory.fourstory_api.repository.tglobal.UserRepository;
import com.fourstory.fourstory_api.repository.tglobal.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailChangeServiceTest {

    @InjectMocks
    EmailChangeService emailChangeService;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OutboxService outboxService;

    @Mock
    private VerificationTokenService verificationTokenService;

    VerificationToken verificationToken;

    @BeforeEach
    void setUp() {
        verificationToken = VerificationToken.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .user(User.builder().id(1).build())
                .tokenType(VerificationTokenType.EMAIL_CHANGE)
                .tokenHash(new byte[] { 1, 2, 3 })
                .expiresAt(Instant.now().plus(Duration.ofHours(1)))
                .newEmail("email@email.com")
                .build();
    }

    @Test
    public void requestEmailChange_shouldThrowException_whenEmailAlreadyInUse() {
        String newEmail = "email@email.com";
        when(userRepository.existsByEmailIgnoreCase(newEmail)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> emailChangeService.requestEmailChange(null, newEmail));

        assertEquals(ErrorCode.EMAIL_ALREADY_IN_USE, exception.getErrorCode());
        verify(userRepository).existsByEmailIgnoreCase(newEmail);
    }

    @Test
    public void requestEmailChange_shouldProceed_whenEmailNotInUse() {
        User user = User.builder()
                .id(1)
                .build();
        String newEmail = "email@email.com";

        when(userRepository.existsByEmailIgnoreCase(newEmail)).thenReturn(false);
        when(verificationTokenService.createAndSaveToken(user, VerificationTokenType.EMAIL_CHANGE, 1)).thenReturn("rawToken");

        emailChangeService.requestEmailChange(user, newEmail);

        verify(userRepository).existsByEmailIgnoreCase(newEmail);
        verify(verificationTokenRepository).invalidateActive(eq(user.getId()), eq(VerificationTokenType.EMAIL_CHANGE), any(Instant.class));
        verify(verificationTokenService).createAndSaveToken(user, VerificationTokenType.EMAIL_CHANGE, 1);
        verify(outboxService).enqueue(OutboxEventType.EMAIL_CHANGE_REQUEST, new EmailChangeRequestedEvent(newEmail, "rawToken"));
    }

    @Test
    public void confirmEmailChange_shouldThrowException_whenTokenInvalid() {
        when(verificationTokenService.getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_CHANGE))
                .thenThrow(new BusinessException(ErrorCode.LINK_INVALID_OR_EXPIRED));
        BusinessException exception = assertThrows(BusinessException.class, () -> emailChangeService.confirmEmailChange("rawToken"));

        assertEquals(ErrorCode.LINK_INVALID_OR_EXPIRED, exception.getErrorCode());
        verify(verificationTokenService).getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_CHANGE);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void confirmEmailChange_shouldThrowException_whenNewEmailIsNull() {
        verificationToken.setNewEmail(null);

        when(verificationTokenService.getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_CHANGE))
                .thenReturn(verificationToken);


        BusinessException exception = assertThrows(BusinessException.class, () -> emailChangeService.confirmEmailChange("rawToken"));

        assertEquals(ErrorCode.EMAIL_ALREADY_IN_USE, exception.getErrorCode());
        verify(verificationTokenService).getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_CHANGE);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void confirmEmailChange_shouldThrowException_whenNewEmailIsUsed() {

        when(verificationTokenService.getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_CHANGE))
                .thenReturn(verificationToken);
        when(userRepository.existsByEmailIgnoreCase(verificationToken.getNewEmail())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> emailChangeService.confirmEmailChange("rawToken"));

        assertEquals(ErrorCode.EMAIL_ALREADY_IN_USE, exception.getErrorCode());
        verify(verificationTokenService).getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_CHANGE);
        verify(userRepository).existsByEmailIgnoreCase(verificationToken.getNewEmail());
    }

    @Test
    public void confirmEmailChange_shouldChangeEmail() {
        when(verificationTokenService.getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_CHANGE))
                .thenReturn(verificationToken);
        when(userRepository.existsByEmailIgnoreCase(verificationToken.getNewEmail())).thenReturn(false);

        emailChangeService.confirmEmailChange("rawToken");

        verify(verificationTokenService).getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_CHANGE);
        verify(userRepository).existsByEmailIgnoreCase(verificationToken.getNewEmail());
        assertEquals(verificationToken.getNewEmail(), verificationToken.getUser().getEmail());
    }
}