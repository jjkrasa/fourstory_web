package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.model.tglobal.User;
import com.fourstory.fourstory_api.repository.tglobal.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmailIgnoreCase("email@email.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("email@email.com"));

        assertEquals("User not found with email: email@email.com", exception.getMessage());
        verify(userRepository).findByEmailIgnoreCase("email@email.com");
    }

    @Test
    public void loadUserByUsername_ShouldReturnUserDetails() {
        User user = User.builder().id(1).email("email@email.com").build();

        when(userRepository.findByEmailIgnoreCase("email@email.com")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("email@email.com");

        assertEquals(user, result);
        verify(userRepository).findByEmailIgnoreCase("email@email.com");
    }
}