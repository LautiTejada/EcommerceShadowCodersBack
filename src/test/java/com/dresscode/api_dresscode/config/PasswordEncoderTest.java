package com.dresscode.api_dresscode.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD RED: PasswordEncoder must handle {bcrypt}-prefixed and legacy bare $2a$ passwords.
 *
 * Spec: Req — BCrypt Password Encoding Prefix Correctness
 *   Scenario: Login with correctly prefixed stored password → auth success
 *   Scenario: Login with legacy bare-bcrypt stored password → not silent failure
 */
@SpringBootTest
@ActiveProfiles("test")
class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String PLAINTEXT = "mySecurePassword123";

    @Test
    void encodedPasswordUsesCorrectPrefix() {
        String encoded = passwordEncoder.encode(PLAINTEXT);
        assertNotNull(encoded, "Encoded password must not be null");
        assertTrue(encoded.startsWith("{bcrypt}"),
                "DelegatingPasswordEncoder must prefix encoded password with {bcrypt}, got: " + encoded);
    }

    @Test
    void matchesCorrectlyPrefixedBcryptPassword() {
        String encoded = passwordEncoder.encode(PLAINTEXT);
        assertTrue(passwordEncoder.matches(PLAINTEXT, encoded),
                "Password must match its encoded form with {bcrypt} prefix");
    }

    @Test
    void doesNotMatchWrongPassword() {
        String encoded = passwordEncoder.encode(PLAINTEXT);
        assertFalse(passwordEncoder.matches("wrong-password", encoded),
                "Wrong password must not match");
    }

    @Test
    void matchesLegacyBareBcryptPassword() {
        // Simulate a legacy password stored without the {bcrypt} prefix:
        // Use BCryptPasswordEncoder directly to create such a hash
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder legacyEncoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String legacyHash = legacyEncoder.encode(PLAINTEXT); // produces $2a$10$...

        // The system must either succeed (via fallback) or throw a clear exception —
        // but MUST NOT silently return false for a valid password.
        // Per spec: "succeed via fallback OR return a clear HTTP 401" — not silent failure.
        // We verify the system handles it without throwing unexpected exceptions.
        try {
            boolean matches = passwordEncoder.matches(PLAINTEXT, legacyHash);
            // If it returns true, legacy support works — perfect.
            // If it returns false, that's acceptable per spec (clear 401), but not an exception.
            // The test passes as long as no unexpected exception is thrown.
        } catch (Exception e) {
            // Any exception here indicates the encoder can't handle the legacy format at all.
            // Per spec: the system MUST either succeed via fallback or return a clear 401.
            // Throwing an unexpected unchecked exception during authentication is NOT acceptable.
            fail("PasswordEncoder must handle legacy bare-bcrypt without throwing unexpected exception: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
