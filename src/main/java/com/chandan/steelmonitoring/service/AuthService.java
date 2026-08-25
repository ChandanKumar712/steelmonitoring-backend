package com.chandan.steelmonitoring.service;
import com.chandan.steelmonitoring.dto.UserDTO;
import com.chandan.steelmonitoring.entity.User;
import com.chandan.steelmonitoring.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.chandan.steelmonitoring.dto.UserResponseDTO;
import com.chandan.steelmonitoring.exception.EmailAlreadyExistsException;
import com.chandan.steelmonitoring.dto.LoginRequest;
import com.chandan.steelmonitoring.dto.LoginResponse;
import com.chandan.steelmonitoring.security.JwtService;

import com.chandan.steelmonitoring.exception.InvalidCredentialsException;


@Service
public class AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponseDTO register(UserDTO dto) {

        logger.info("Registering user with email : {}", dto.getEmail());

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.warn("Registration failed - email already exists : {}", dto.getEmail());
            throw new EmailAlreadyExistsException(
                    "Email already registered : " + dto.getEmail());
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        User savedUser = userRepository.save(user);

        logger.info("User registered successfully with id : {}", savedUser.getId());

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public LoginResponse login(LoginRequest request) {

        logger.info("Login attempt for email : {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login failed - wrong password for email : {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        logger.info("Login successful for email : {}", request.getEmail());

        return new LoginResponse(token, user.getEmail(), user.getRole());
    }
}