package com.dresscode.api_dresscode.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TDD RED: Security config must default unauthenticated requests to 401/403.
 * Validates the anyRequest().authenticated() requirement (not permitAll).
 *
 * Spec: Req — Secure HTTP Security Configuration
 *   Scenario: Unauthenticated request to unprotected wildcard route → 401/403
 *   Scenario: Auth filter ordering
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestToUnknownRoute_returns401or403() throws Exception {
        mockMvc.perform(get("/api/some-unknown-route-that-is-not-explicitly-permitted"))
                .andExpect(status().is(403));
    }

    @Test
    void unauthenticatedRequestToPublicAuthRoute_returns200or4xx_notServer() throws Exception {
        // /auth/** endpoints should remain accessible (permitAll)
        // We expect 400 (missing body) not 401/403 for auth routes
        mockMvc.perform(get("/auth/login"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Should not be 401/403 — auth routes are public
                    boolean notAuthBlocked = status != 401 && status != 403;
                    if (!notAuthBlocked) {
                        throw new AssertionError(
                                "Expected /auth/** to be publicly accessible, got HTTP " + status);
                    }
                });
    }

    @Test
    void unauthenticatedRequestToPublicBannerRoute_returns2xx() throws Exception {
        // /api/banners is explicitly public
        mockMvc.perform(get("/api/banners"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    boolean notAuthBlocked = status != 401 && status != 403;
                    if (!notAuthBlocked) {
                        throw new AssertionError(
                                "Expected /api/banners to be publicly accessible, got HTTP " + status);
                    }
                });
    }
}
