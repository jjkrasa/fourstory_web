package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.event.VerificationResentEvent;
import com.fourstory.fourstory_api.model.tglobal.OutboxEventType;
import com.fourstory.fourstory_api.model.tglobal.User;
import com.fourstory.fourstory_api.model.tglobal.VerificationToken;
import com.fourstory.fourstory_api.model.tglobal.VerificationTokenType;
import com.fourstory.fourstory_api.repository.tglobal.UserRepository;
import com.fourstory.fourstory_api.repository.tglobal.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final VerificationTokenRepository verificationTokenRepository;

    private final UserRepository userRepository;

    private final OutboxService outboxService;

    private final VerificationTokenService verificationTokenService;

    @Transactional(transactionManager = "tglobalTransactionManager")
    public void verifyEmail(String rawToken) {
        VerificationToken verificationToken = verificationTokenService.getActiveTokenByHashAndTypeOrThrowException(rawToken, VerificationTokenType.EMAIL_VERIFY);

        User user = verificationToken.getUser();
        if (!user.getEmailVerified()) {
            user.setEmailVerified(true);
        }

        verificationToken.setConsumedAt(Instant.now());
    }

    @Transactional(transactionManager = "tglobalTransactionManager")
    public void resendVerificationEmail(String email) {
        userRepository.findByEmailIgnoreCase(email)
                .filter(user -> !user.getEmailVerified())
                .ifPresent(user -> {
                    verificationTokenRepository.invalidateActive(
                            user.getId(),
                            VerificationTokenType.EMAIL_VERIFY,
                            Instant.now()
                    );

                    String rawToken = verificationTokenService.createAndSaveToken(user, VerificationTokenType.EMAIL_VERIFY, 24);

                    outboxService.enqueue(OutboxEventType.EMAIL_VERIFICATION_REQUEST, new VerificationResentEvent(user.getEmail(), rawToken));
                });
    }
}
