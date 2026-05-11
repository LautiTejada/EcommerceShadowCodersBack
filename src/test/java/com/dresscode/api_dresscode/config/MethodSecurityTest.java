package com.dresscode.api_dresscode.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TDD RED: State-mutating endpoints must reject unauthorized roles.
 *
 * Spec: Req — Method-Level Authorization on All Sensitive Endpoints
 *   Scenario: Unauthorized call blocked at method level → 403
 *   Scenario: Authorized call passes method security → 200
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MethodSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void estadisticasDashboard_asUser_returns403() throws Exception {
        // /api/estadisticas/** is ADMIN-only; a USER must be rejected
        mockMvc.perform(get("/api/estadisticas/dashboard")
                .with(user("testuser").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void estadisticasDashboard_asAdmin_returns2xx() throws Exception {
        mockMvc.perform(get("/api/estadisticas/dashboard")
                .with(user("adminuser").roles("ADMIN")))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // 200 or 5xx (service might fail without DB in test) but NOT 403
                    assertNotForbidden(status, "/api/estadisticas/dashboard as ADMIN");
                });
    }

    @Test
    void ordenesMutating_asUnauthenticated_returns401or403() throws Exception {
        mockMvc.perform(post("/api/ordenes/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status == 401 || status == 403,
                            "Unauthenticated POST to /api/ordenes/crear must return 401 or 403, got " + status);
                });
    }

    private void assertNotForbidden(int status, String context) {
        if (status == 403) {
            throw new AssertionError("Expected non-403 for " + context + ", but got 403");
        }
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
