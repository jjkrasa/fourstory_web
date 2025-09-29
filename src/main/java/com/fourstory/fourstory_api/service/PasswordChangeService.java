package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.event.PasswordChangedEvent;
import com.fourstory.fourstory_api.exception.BusinessException;
import com.fourstory.fourstory_api.exception.ErrorCode;
import com.fourstory.fourstory_api.model.tglobal.OutboxEventType;
import com.fourstory.fourstory_api.model.tglobal.User;
import com.fourstory.fourstory_api.repository.tglobal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordChangeService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final OutboxService outboxService;

    @Transactional(transactionManager = "tglobalTransactionManager")
    public void changePassword(User principal, String currentPassword, String newPassword) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + principal.getEmail())
        );

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.WRONG_CURRENT_PASSWORD);
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        outboxService.enqueue(OutboxEventType.PASSWORD_CHANGED, new PasswordChangedEvent(user.getEmail()));
    }
}
