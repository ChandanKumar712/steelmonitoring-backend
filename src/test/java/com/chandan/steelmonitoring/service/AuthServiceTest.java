package com.chandan.steelmonitoring.service;

import com.chandan.steelmonitoring.dto.LoginRequest;
import com.chandan.steelmonitoring.dto.LoginResponse;
import com.chandan.steelmonitoring.dto.UserDTO;
import com.chandan.steelmonitoring.dto.UserResponseDTO;
import com.chandan.steelmonitoring.entity.User;
import com.chandan.steelmonitoring.exception.EmailAlreadyExistsException;
import com.chandan.steelmonitoring.exception.InvalidCredentialsException;
import com.chandan.steelmonitoring.repository.UserRepository;
import com.chandan.steelmonitoring.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // ---------- REGISTER TESTS ----------

    @Test
    void shouldRegisterUser_whenEmailNotAlreadyUsed() {

        // ARRANGE
        UserDTO dto = new UserDTO();
        dto.setName("Chandan");
        dto.setEmail("abc@gmail.com");
        dto.setPassword("123456");
        dto.setRole("ADMIN");

        when(userRepository.findByEmail("abc@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("encodedPassword123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Chandan");
        savedUser.setEmail("abc@gmail.com");
        savedUser.setPassword("encodedPassword123");
        savedUser.setRole("ADMIN");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // ACT
        UserResponseDTO result = authService.register(dto);

        // ASSERT
        assertEquals("Chandan", result.getName());
        assertEquals("abc@gmail.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
    }

    @Test
    void shouldThrowException_whenEmailAlreadyExists() {

        // ARRANGE
        UserDTO dto = new UserDTO();
        dto.setEmail("abc@gmail.com");

        when(userRepository.findByEmail("abc@gmail.com"))
                .thenReturn(Optional.of(new User()));

        // ACT + ASSERT
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(dto);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    // ---------- LOGIN TESTS ----------

    @Test
    void shouldLoginSuccessfully_whenCredentialsAreValid() {

        // ARRANGE
        LoginRequest request = new LoginRequest();
        request.setEmail("abc@gmail.com");
        request.setPassword("123456");

        User existingUser = new User();
        existingUser.setEmail("abc@gmail.com");
        existingUser.setPassword("encodedPassword123");
        existingUser.setRole("ADMIN");

        when(userRepository.findByEmail("abc@gmail.com"))
                .thenReturn(Optional.of(existingUser));

        when(passwordEncoder.matches("123456", "encodedPassword123"))
                .thenReturn(true);

        when(jwtService.generateToken("abc@gmail.com"))
                .thenReturn("fake-jwt-token");

        // ACT
        LoginResponse result = authService.login(request);

        // ASSERT
        assertEquals("fake-jwt-token", result.getToken());
        assertEquals("abc@gmail.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
    }

    @Test
    void shouldThrowException_whenPasswordIsInvalid() {

        // ARRANGE
        LoginRequest request = new LoginRequest();
        request.setEmail("abc@gmail.com");
        request.setPassword("wrongPassword");

        User existingUser = new User();
        existingUser.setEmail("abc@gmail.com");
        existingUser.setPassword("encodedPassword123");

        when(userRepository.findByEmail("abc@gmail.com"))
                .thenReturn(Optional.of(existingUser));

        when(passwordEncoder.matches("wrongPassword", "encodedPassword123"))
                .thenReturn(false);

        // ACT + ASSERT
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(request);
        });

        verify(jwtService, never()).generateToken(anyString());
    }
}