package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.exception.BusinessException;
import com.fourstory.fourstory_api.exception.ErrorCode;
import com.fourstory.fourstory_api.model.tglobal.User;
import com.fourstory.fourstory_api.model.tglobal.VerificationToken;
import com.fourstory.fourstory_api.model.tglobal.VerificationTokenType;
import com.fourstory.fourstory_api.repository.tglobal.VerificationTokenRepository;
import com.fourstory.fourstory_api.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    private final TokenUtil tokenUtil;

    public VerificationToken getActiveTokenByHashAndTypeOrThrowException(String rawToken, VerificationTokenType tokenType) {
        return verificationTokenRepository.findActive(tokenUtil.sha256(rawToken), tokenType, Instant.now())
                .orElseThrow(() -> new BusinessException(ErrorCode.LINK_INVALID_OR_EXPIRED));
    }

    public String createAndSaveToken(User user, VerificationTokenType tokenType, int hours) {
        String rawToken = tokenUtil.generateRaw();

        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .tokenType(tokenType)
                .tokenHash(tokenUtil.sha256(rawToken))
                .expiresAt(Instant.now().plus(tokenUtil.hours(hours)))
                .build();

        verificationTokenRepository.save(verificationToken);

        return rawToken;
    }
}
