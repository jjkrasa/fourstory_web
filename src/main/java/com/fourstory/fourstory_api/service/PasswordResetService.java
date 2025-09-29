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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final VerificationTokenRepository verificationTokenRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final OutboxService outboxService;

    private final VerificationTokenService verificationTokenService;


    @Transactional(transactionManager = "tglobalTransactionManager")
    public void requestForgotPassword(String email) {
        userRepository.findByEmailIgnoreCase(email)
                .ifPresent(user -> {
                    verificationTokenRepository.invalidateActive(
                            user.getId(),
                            VerificationTokenType.PASSWORD_RESET,
                            Instant.now()
                    );

                    String rawToken = verificationTokenService.createAndSaveToken(user, VerificationTokenType.PASSWORD_RESET, 1);

                    outboxService.enqueue(OutboxEventType.PASSWORD_RESET_REQUEST, new PasswordResetRequestedEvent(user.getEmail(), rawToken));
                });
    }

    @Transactional(transactionManager = "tglobalTransactionManager")
    public void confirmPasswordResetChange(String rawToken, String newPassword) {

        VerificationToken verificationToken = verificationTokenService.getActiveTokenByHashAndTypeOrThrowException(rawToken, VerificationTokenType.PASSWORD_RESET);

        User user = verificationToken.getUser();

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        verificationToken.setConsumedAt(Instant.now());
    }
}
