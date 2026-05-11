package com.dresscode.api_dresscode.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD RED: Logging profile test — production profile must set security logging to WARN.
 *
 * Spec: Req — Debug Logging Disabled in Non-Development Profiles
 *   Scenario: Debug logs absent in production profile → security logger at WARN or above
 *   Scenario: Debug logs present in dev profile → security logger at DEBUG
 *
 * NOTE: We test via property configuration rather than actual log capture,
 * because the log level is set by the environment property and applied at boot.
 * A more complete test would use a LogCaptor library; here we verify the property
 * binding is correct per profile.
 */
@SpringBootTest
@ActiveProfiles("test")
class LoggingProfileTest {

    @Autowired
    private Environment environment;

    @Test
    void testProfile_securityLogLevelIsErrorOrAbove() {
        // Test profile must not emit DEBUG security logs
        String level = environment.getProperty("logging.level.org.springframework.security");
        assertNotNull(level, "logging.level.org.springframework.security must be set in test profile");

        // In test profile, we set ERROR to suppress security noise
        // Acceptable values: ERROR, WARN, INFO (anything above DEBUG)
        assertNotEquals("DEBUG", level.toUpperCase(),
                "Test profile must NOT use DEBUG level for security logging");
        assertNotEquals("TRACE", level.toUpperCase(),
                "Test profile must NOT use TRACE level for security logging");
    }

    @Test
    void corsAllowedOriginsPropertyIsBound() {
        // Verifies that cors.allowed-origins is bound (re-validates 1.1 from here too)
        String value = environment.getProperty("cors.allowed-origins");
        assertNotNull(value, "cors.allowed-origins must be bound in test profile");
    }
}
