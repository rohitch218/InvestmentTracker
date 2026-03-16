package com.investtracker.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/portfolio")
    public Mono<Map<String, String>> portfolioFallback() {
        return Mono.just(Map.of(
            "status", "Service Unavailable",
            "message", "Portfolio service is currently busy or down. Please try again later.",
            "code", "SERVICE_MAINTENANCE_PORTFOLIO"
        ));
    }

    @GetMapping("/fallback/transaction")
    public Mono<Map<String, String>> transactionFallback() {
        return Mono.just(Map.of(
            "status", "Service Unavailable",
            "message", "Transaction service is currently unreachable. Financial events cannot be logged at this moment.",
            "code", "SERVICE_MAINTENANCE_TRANSACTION"
        ));
    }

    @GetMapping("/fallback/auth")
    public Mono<Map<String, String>> authFallback() {
        return Mono.just(Map.of(
            "status", "Service Unavailable",
            "message", "Authentication service is temporarily unavailable. Login and registration are currently disabled.",
            "code", "SERVICE_MAINTENANCE_AUTH"
        ));
    }
}
