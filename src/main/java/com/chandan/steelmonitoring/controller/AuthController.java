package com.chandan.steelmonitoring.controller;

import com.chandan.steelmonitoring.dto.UserDTO;
import com.chandan.steelmonitoring.dto.UserResponseDTO;
import com.chandan.steelmonitoring.entity.User;
import com.chandan.steelmonitoring.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
////   For Login
import com.chandan.steelmonitoring.dto.LoginRequest;
import com.chandan.steelmonitoring.dto.LoginResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    ////   signup
    @PostMapping("/register")
    public UserResponseDTO register(@Valid @RequestBody UserDTO dto) {
        return authService.register(dto);
    }

    ////   Login
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}