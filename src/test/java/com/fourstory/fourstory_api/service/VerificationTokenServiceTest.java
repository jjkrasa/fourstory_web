package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.exception.BusinessException;
import com.fourstory.fourstory_api.exception.ErrorCode;
import com.fourstory.fourstory_api.model.tglobal.VerificationToken;
import com.fourstory.fourstory_api.model.tglobal.VerificationTokenType;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceTest {

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private TokenUtil tokenUtil;

    @Nested
    class GetActiveTokenByHashAndTypeOrThrowException {

        @Test
        public void getActiveTokenByHashAndTypeOrThrowException_shouldThrowBusinessException_whenTokenNotFound() {
            byte[] tokenHash = new byte[]{ 1, 2, 3 };

            when(tokenUtil.sha256("rawToken")).thenReturn(tokenHash);
            when(verificationTokenRepository.findActive(eq(tokenHash),  eq(VerificationTokenType.EMAIL_CHANGE), any(Instant.class)))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class, () -> verificationTokenService.getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_CHANGE));

            assertEquals(ErrorCode.LINK_INVALID_OR_EXPIRED, exception.getErrorCode());
            verify(tokenUtil).sha256("rawToken");
            verify(verificationTokenRepository).findActive(eq(tokenHash),  eq(VerificationTokenType.EMAIL_CHANGE), any(Instant.class));
        }

        @Test
        public void getActiveTokenByHashAndTypeOrThrowException_shouldReturnVerificationToken() {
            byte[] tokenHash = new byte[]{1, 2, 3};

            VerificationToken verificationToken = new VerificationToken();

            when(tokenUtil.sha256("rawToken")).thenReturn(tokenHash);
            when(verificationTokenRepository.findActive(eq(tokenHash), eq(VerificationTokenType.EMAIL_CHANGE), any(Instant.class)))
                    .thenReturn(Optional.of(verificationToken));

            VerificationToken result = verificationTokenService.getActiveTokenByHashAndTypeOrThrowException("rawToken", VerificationTokenType.EMAIL_CHANGE);

            assertEquals(verificationToken, result);
            verify(tokenUtil).sha256("rawToken");
            verify(verificationTokenRepository).findActive(eq(tokenHash), eq(VerificationTokenType.EMAIL_CHANGE), any(Instant.class));
        }
    }

    @Nested
    class CreateAndSaveToken {

        @Test
        void createAndSaveToken_shouldCreateAndSaveToken() {
            byte[] tokenHash = new byte[]{1, 2, 3};

            when(tokenUtil.generateRaw()).thenReturn("rawToken");
            when(tokenUtil.sha256("rawToken")).thenReturn(tokenHash);
            when(tokenUtil.hours(2)).thenReturn(Duration.ofHours(2));

            String rawToken = verificationTokenService.createAndSaveToken(null, VerificationTokenType.EMAIL_CHANGE, 2);

            assertEquals("rawToken", rawToken);
            verify(tokenUtil).generateRaw();
            verify(tokenUtil).sha256("rawToken");
            verify(tokenUtil).hours(2);
            verify(verificationTokenRepository).save(any(VerificationToken.class));
        }
    }
}