package com.investtracker.auth.service;

import com.investtracker.auth.client.AuditClient;
import com.investtracker.auth.dto.AuthResponse;
import com.investtracker.auth.dto.LoginRequest;
import com.investtracker.auth.dto.RegisterRequest;
import com.investtracker.auth.dto.audit.AuditRequest;
import com.investtracker.auth.entity.User;
import com.investtracker.auth.exception.BusinessException;
import com.investtracker.auth.repository.UserRepository;
import com.investtracker.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * AuthService — Refactored for Phase 5 (Event-Driven Auditing).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final RestTemplate restTemplate;
    private final AuditClient auditClient;

    @Value("${app.tenant-service.url:http://tenant-service:8082}")
    private String tenantServiceUrl;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Calling Tenant Service to register tenant: {}", request.getCompanyName());
        Map<String, Object> tenantRequest = Map.of("name", request.getCompanyName());
        
        Map tenantResponse = restTemplate.postForObject(
            tenantServiceUrl + "/api/v1/tenants",
            tenantRequest,
            Map.class
        );

        if (tenantResponse == null || !tenantResponse.containsKey("id")) {
            throw new BusinessException("Failed to register tenant. Tenant Service returned invalid response.");
        }

        String tenantId = (String) tenantResponse.get("id");

        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .username(request.getUsername())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .tenantId(tenantId)
            .role(User.Role.ADMIN)
            .isActive(true)
            .build();

        User saved = userRepository.save(user);

        // Asynchronous Audit logging for registration
        auditClient.log(AuditRequest.builder()
            .userId(saved.getId())
            .entityType("User")
            .entityId(saved.getId())
            .action(AuditRequest.AuditAction.CREATE)
            .newValue("User registered and tenant created")
            .build(), tenantId);

        log.info("Created user: {} for tenant: {}", user.getEmail(), tenantId);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("User not found after successful authentication"));

        if (!user.getIsActive()) {
            throw new BusinessException("Account is deactivated");
        }

        // Asynchronous Audit logging for login event
        auditClient.log(AuditRequest.builder()
            .userId(user.getId())
            .entityType("User")
            .entityId(user.getId())
            .action(AuditRequest.AuditAction.LOGIN)
            .build(), user.getTenantId());

        log.info("User logged in: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(
            user.getId(), 
            user.getEmail(), 
            user.getRole().name(), 
            user.getTenantId()
        );
        String refreshToken = jwtUtil.generateRefreshToken(
            user.getId(), 
            user.getEmail(), 
            user.getTenantId()
        );

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .user(AuthResponse.UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .tenantId(user.getTenantId())
                .build())
            .build();
    }
}
