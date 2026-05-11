package com.dresscode.api_dresscode.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD RED: Validates that CORS allowed-origins property is bound from configuration.
 * Also validates that profile-specific overrides apply.
 */
@SpringBootTest
@ActiveProfiles("test")
class CorsPropertiesTest {

    @Autowired
    private Environment environment;

    @Test
    void corsAllowedOriginsPropertyExists() {
        String value = environment.getProperty("cors.allowed-origins");
        assertNotNull(value, "cors.allowed-origins must be defined in application properties");
        assertFalse(value.isBlank(), "cors.allowed-origins must not be blank");
    }

    @Test
    void webhookHmacSecretPropertyExists() {
        String value = environment.getProperty("webhook.hmac-secret");
        assertNotNull(value, "webhook.hmac-secret must be defined in application properties");
        assertFalse(value.isBlank(), "webhook.hmac-secret must not be blank");
    }

    @Test
    void corsAllowedOriginsDoesNotContainWildcard() {
        String value = environment.getProperty("cors.allowed-origins");
        if (value != null) {
            assertFalse(value.contains("*"),
                    "cors.allowed-origins must NOT contain a wildcard '*' — use explicit origins");
        }
    }
}
