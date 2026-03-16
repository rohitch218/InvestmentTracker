package com.investtracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthResponse — Returned by login and register endpoints.
 *
 * WHY return accessToken in body but refresh in HttpOnly cookie?
 * Access token in body: JS can read it to attach to API requests.
 * Refresh token in HttpOnly cookie: JS cannot read it at all — immune to XSS theft.
 * This is the industry-standard "split token" approach.
 *
 * (Cookie setting is handled in the controller via HttpServletResponse)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long   expiresIn;      // seconds
    private UserInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long   id;
        private String email;
        private String username;
        private String role;
        private String tenantId;
    }
}
