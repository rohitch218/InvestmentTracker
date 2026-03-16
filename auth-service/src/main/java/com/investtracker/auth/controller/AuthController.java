package com.investtracker.auth.controller;

import com.investtracker.auth.dto.AuthResponse;
import com.investtracker.auth.dto.LoginRequest;
import com.investtracker.auth.dto.RegisterRequest;
import com.investtracker.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Stateless JWT logout is handled client-side (clearing the token).
        // This endpoint provides a success response for frontend consistency.
        return ResponseEntity.ok().build();
    }
}
