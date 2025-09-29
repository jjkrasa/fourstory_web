package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.dto.request.LoginRequest;
import com.fourstory.fourstory_api.dto.request.RegisterRequest;
import com.fourstory.fourstory_api.event.VerificationResentEvent;
import com.fourstory.fourstory_api.exception.BusinessException;
import com.fourstory.fourstory_api.exception.ErrorCode;
import com.fourstory.fourstory_api.mapper.UserMapper;
import com.fourstory.fourstory_api.model.tglobal.OutboxEventType;
import com.fourstory.fourstory_api.model.tglobal.User;
import com.fourstory.fourstory_api.model.tglobal.VerificationTokenType;
import com.fourstory.fourstory_api.repository.tglobal.UserRepository;
import com.fourstory.fourstory_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final OutboxService outboxService;

    private final VerificationTokenService verificationTokenService;

    @Transactional(readOnly = true, transactionManager = "tglobalTransactionManager")
    public String login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) auth.getPrincipal();

        if (!user.getEmailVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        return jwtService.generateAccessToken(user.getEmail());
    }

    @Transactional(transactionManager = "tglobalTransactionManager")
    public void register(RegisterRequest request) {
        checkUserNameAndEmailUniqueness(request.getUserName(), request.getEmail());
        checkPasswordsMatch(request.getPassword(), request.getConfirmPassword());

        User user = userMapper.registerRequestToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String rawToken = verificationTokenService.createAndSaveToken(user, VerificationTokenType.EMAIL_VERIFY, 24);

        outboxService.enqueue(OutboxEventType.EMAIL_VERIFICATION_REQUEST, new VerificationResentEvent(user.getEmail(), rawToken));
    }

    private void checkUserNameAndEmailUniqueness(String userName, String email) {
        if (userRepository.existsByUserNameIgnoreCase(userName)) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_IN_USE, userName);
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_IN_USE, email);
        }
    }

    private void checkPasswordsMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }
    }
}
