package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.dto.request.LoginRequest;
import com.fourstory.fourstory_api.dto.request.RegisterRequest;
import com.fourstory.fourstory_api.exception.BusinessException;
import com.fourstory.fourstory_api.exception.ErrorCode;
import com.fourstory.fourstory_api.mapper.UserMapper;
import com.fourstory.fourstory_api.model.tglobal.OutboxEventType;
import com.fourstory.fourstory_api.model.tglobal.Role;
import com.fourstory.fourstory_api.model.tglobal.User;
import com.fourstory.fourstory_api.model.tglobal.VerificationTokenType;
import com.fourstory.fourstory_api.repository.tglobal.UserRepository;
import com.fourstory.fourstory_api.repository.tglobal.VerificationTokenRepository;
import com.fourstory.fourstory_api.security.JwtService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OutboxService outboxService;

    @Mock
    private VerificationTokenService verificationTokenService;

    private LoginRequest loginRequest;

    private RegisterRequest registerRequest;

    private User user;

    @BeforeEach
    void setUp() {
        String email = "email@email.com";
        loginRequest = LoginRequest.builder()
                .email(email)
                .password("Password123!")
                .build();

        registerRequest = RegisterRequest.builder()
                .userName("username")
                .email(email)
                .password("Password123!")
                .confirmPassword("Password123!")
                .build();

        user = User.builder()
                .id(1)
                .userName("username")
                .email(email)
                .registrationEmail(email)
                .password(null)
                .emailVerified(false)
                .role(Role.USER)
                .checkFlag((byte) 0)
                .firstLogin(null)
                .lastLogin(null)
                .build();
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        void login_shouldSuccessAndGenerateAccessToken() {
            Authentication authentication = Mockito.mock(Authentication.class);
            user.setEmailVerified(true);

            when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);
            when(jwtService.generateAccessToken("email@email.com")).thenReturn("jwt-token");

            String token = authService.login(loginRequest);

            Assertions.assertEquals("jwt-token", token);

            verify(authenticationManager).authenticate(Mockito.any());
            verify(jwtService).generateAccessToken("email@email.com");
        }

        @Test
        void login_shouldThrowException_whenEmailIsNotVerified() {
            Authentication authentication = Mockito.mock(Authentication.class);

            when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(user);

            BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(loginRequest));

            assertEquals(ErrorCode.EMAIL_NOT_VERIFIED, exception.getErrorCode());
            verify(authenticationManager).authenticate(Mockito.any());
            verify(jwtService, never()).generateAccessToken(Mockito.anyString());
        }
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        void register_shouldThrowException_whenUserNameIsAlreadyInUse() {
            when(userRepository.existsByUserNameIgnoreCase("username")).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(registerRequest));

            assertEquals(ErrorCode.USERNAME_ALREADY_IN_USE, exception.getErrorCode());
            verify(userRepository).existsByUserNameIgnoreCase("username");
            verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder, verificationTokenRepository, outboxService);
        }

        @Test
        void register_shouldThrowException_whenEmailIsAlreadyInUse() {
            when(userRepository.existsByUserNameIgnoreCase("username")).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase("email@email.com")).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(registerRequest));

            assertEquals(ErrorCode.EMAIL_ALREADY_IN_USE, exception.getErrorCode());
            verify(userRepository).existsByUserNameIgnoreCase("username");
            verify(userRepository).existsByEmailIgnoreCase("email@email.com");
            verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder, verificationTokenRepository, outboxService);
        }

        @Test
        void register_shouldThrowException_whenPasswordsDontMatch() {
            registerRequest.setConfirmPassword("DifferentPassword123!");

            when(userRepository.existsByUserNameIgnoreCase("username")).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase("email@email.com")).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(registerRequest));

            assertEquals(ErrorCode.PASSWORDS_DO_NOT_MATCH, exception.getErrorCode());
            verify(userRepository).existsByUserNameIgnoreCase("username");
            verify(userRepository).existsByEmailIgnoreCase("email@email.com");
            verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder, verificationTokenRepository, outboxService);
        }

        @Test
        void register_shouldRegisterUser() {
            when(userRepository.existsByUserNameIgnoreCase("username")).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase("email@email.com")).thenReturn(false);
            when(userMapper.registerRequestToUser(registerRequest)).thenReturn(user);
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("CryptedPassword123!");
            when(verificationTokenService.createAndSaveToken(user, VerificationTokenType.EMAIL_VERIFY, 24)).thenReturn("raw-token");

            authService.register(registerRequest);

            assertEquals("CryptedPassword123!", user.getPassword());

            verify(userRepository).existsByUserNameIgnoreCase("username");
            verify(userRepository).existsByEmailIgnoreCase("email@email.com");
            verify(userMapper).registerRequestToUser(registerRequest);
            verify(passwordEncoder).encode(registerRequest.getPassword());
            verify(userRepository).save(user);
            verify(verificationTokenService).createAndSaveToken(user, VerificationTokenType.EMAIL_VERIFY, 24);
            verify(outboxService).enqueue(Mockito.eq(OutboxEventType.EMAIL_VERIFICATION_REQUEST), Mockito.any());
        }
    }
}