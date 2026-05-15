package com.dresscode.api_dresscode.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TDD RED: Input validation must return HTTP 400 with structured error body.
 *
 * Spec: Req — Input Validation on All Request Bodies
 *   Scenario: Invalid payload rejected → HTTP 400 + field-level errors
 *   Scenario: Valid payload accepted → not 400
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InputValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerWithMissingUsername_returns400WithFieldError() throws Exception {
        String payload = """
                {
                  "password": "validPassword123",
                  "email": "valid@test.com"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void registerWithMissingEmail_returns400WithFieldError() throws Exception {
        String payload = """
                {
                  "username": "validuser",
                  "password": "validPassword123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void loginWithMissingCredentials_returns400WithFieldError() throws Exception {
        String payload = """
                {
                  "username": ""
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerWithValidPayload_isNotRejectedWith400() throws Exception {
        // Valid payload should not be rejected at validation level
        // (it may fail at business level with a different error, but not 400 from validation)
        String payload = """
                {
                  "username": "validuser123",
                  "password": "validPassword123",
                  "email": "valid123@test.com"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // 400 specifically from validation would indicate field errors
                    // We accept 200 (success), 409 (conflict), 500 (DB not available in test)
                    // but NOT 400 from validation when all fields are present and valid
                    if (status == 400) {
                        String body = result.getResponse().getContentAsString();
                        if (body.contains("username") || body.contains("password") || body.contains("email")) {
                            throw new AssertionError(
                                    "Valid payload must not trigger validation errors, got: " + body);
                        }
                    }
                });
    }
}
