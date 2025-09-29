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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailChangeService {

    private final VerificationTokenRepository verificationTokenRepository;

    private final UserRepository userRepository;

    private final OutboxService outboxService;

    private final VerificationTokenService verificationTokenService;

    @Transactional(transactionManager = "tglobalTransactionManager")
    public void requestEmailChange(User user, String newEmail) {
        if (userRepository.existsByEmailIgnoreCase(newEmail)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_IN_USE);
        }

        verificationTokenRepository.invalidateActive(user.getId(), VerificationTokenType.EMAIL_CHANGE, Instant.now());

        String rawToken = verificationTokenService.createAndSaveToken(user, VerificationTokenType.EMAIL_CHANGE, 1);

        outboxService.enqueue(OutboxEventType.EMAIL_CHANGE_REQUEST, new EmailChangeRequestedEvent(newEmail, rawToken));
    }

    @Transactional(transactionManager = "tglobalTransactionManager")
    public void confirmEmailChange(String rawToken) {
        VerificationToken verificationToken = verificationTokenService.getActiveTokenByHashAndTypeOrThrowException(rawToken, VerificationTokenType.EMAIL_CHANGE);

        String newEmail = verificationToken.getNewEmail();

        if (newEmail == null || userRepository.existsByEmailIgnoreCase(newEmail)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_IN_USE);
        }

        verificationToken.getUser().setEmail(newEmail);
        verificationToken.setConsumedAt(Instant.now());
    }
}
