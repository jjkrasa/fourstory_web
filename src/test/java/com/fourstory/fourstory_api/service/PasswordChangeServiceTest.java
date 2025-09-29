package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.event.PasswordChangedEvent;
import com.fourstory.fourstory_api.exception.BusinessException;
import com.fourstory.fourstory_api.exception.ErrorCode;
import com.fourstory.fourstory_api.model.tglobal.OutboxEventType;
import com.fourstory.fourstory_api.model.tglobal.User;
import com.fourstory.fourstory_api.repository.tglobal.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordChangeServiceTest {

    @InjectMocks
    private PasswordChangeService passwordChangeService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OutboxService outboxService;

    private User principal;

    @BeforeEach
    void setUp() {
        principal = User.builder().id(1).email("email@email.com").password("Password").build();
    }

    @Test
    public void changePassword_shouldThrowException_whenUserNotFound() {

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                passwordChangeService.changePassword(principal, "Password", "NewPassword")
        );

        assertEquals("User not found with email: " + principal.getEmail(), exception.getMessage());
        verify(userRepository).findById(1);
    }

    @Test
    public void changePassword_shouldThrowException_whenCurrentPasswordIsWrong() {
        when(userRepository.findById(1)).thenReturn(Optional.of(principal));
        when(passwordEncoder.matches("WrongPassword", principal.getPassword())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                passwordChangeService.changePassword(principal, "WrongPassword", "NewPassword")
        );

        assertEquals(ErrorCode.WRONG_CURRENT_PASSWORD, exception.getErrorCode());
        verify(userRepository).findById(1);
        verify(passwordEncoder).matches("WrongPassword", principal.getPassword());
    }

    @Test
    public void changePassword_shouldThrowException_whenNewPasswordIsSameAsCurrent() {
        when(userRepository.findById(1)).thenReturn(Optional.of(principal));
        when(passwordEncoder.matches("Password", principal.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("Password", principal.getPassword())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                passwordChangeService.changePassword(principal, "Password", "Password")
        );

        assertEquals(ErrorCode.PASSWORD_SAME_AS_OLD, exception.getErrorCode());
        verify(userRepository).findById(1);
        verify(passwordEncoder, times(2)).matches("Password", principal.getPassword());
    }

    @Test
    public void changePassword_shouldSetNewPassword() {
        when(userRepository.findById(1)).thenReturn(Optional.of(principal));
        when(passwordEncoder.matches("Password", principal.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("NewPassword", principal.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("NewPassword")).thenReturn("EncodedNewPassword");

        passwordChangeService.changePassword(principal, "Password", "NewPassword");

        assertEquals("EncodedNewPassword", principal.getPassword());
        verify(userRepository).findById(1);
        verify(passwordEncoder).matches("Password", "Password");
        verify(passwordEncoder).matches("NewPassword", "Password");
        verify(passwordEncoder).encode("NewPassword");
        verify(outboxService).enqueue(eq(OutboxEventType.PASSWORD_CHANGED), any(PasswordChangedEvent.class));
    }
}