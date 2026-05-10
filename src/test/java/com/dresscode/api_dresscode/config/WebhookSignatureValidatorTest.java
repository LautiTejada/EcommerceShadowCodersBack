package com.dresscode.api_dresscode.config;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD RED: HMAC-SHA256 webhook signature validator — pure unit tests.
 *
 * Spec: Req — Webhook Request Integrity Validation
 *   Scenario: Valid signature accepted
 *   Scenario: Missing signature rejected
 *   Scenario: Invalid (tampered) signature rejected
 *
 * Security note: comparison MUST use MessageDigest.isEqual (timing-safe).
 */
class WebhookSignatureValidatorTest {

    private static final String SECRET = "test-secret-key";

    private final WebhookSignatureValidator validator = new WebhookSignatureValidator(SECRET);

    @Test
    void validSignature_returnsTrue() throws Exception {
        String payload = "{\"id\":123,\"type\":\"payment\"}";
        String signature = computeHmacSha256(payload, SECRET);

        boolean result = validator.isValid(payload.getBytes(StandardCharsets.UTF_8), signature);

        assertTrue(result, "A correctly computed HMAC-SHA256 signature must be accepted");
    }

    @Test
    void tamperedPayload_returnsFalse() throws Exception {
        String originalPayload = "{\"id\":123,\"type\":\"payment\"}";
        String tamperedPayload = "{\"id\":999,\"type\":\"payment\"}";
        String signatureForOriginal = computeHmacSha256(originalPayload, SECRET);

        boolean result = validator.isValid(tamperedPayload.getBytes(StandardCharsets.UTF_8), signatureForOriginal);

        assertFalse(result, "A signature computed for original payload must NOT validate tampered payload");
    }

    @Test
    void nullSignature_returnsFalse() {
        byte[] payload = "{\"id\":123}".getBytes(StandardCharsets.UTF_8);

        boolean result = validator.isValid(payload, null);

        assertFalse(result, "A null signature must be rejected");
    }

    @Test
    void blankSignature_returnsFalse() {
        byte[] payload = "{\"id\":123}".getBytes(StandardCharsets.UTF_8);

        boolean result = validator.isValid(payload, "   ");

        assertFalse(result, "A blank signature must be rejected");
    }

    @Test
    void wrongSecret_returnsFalse() throws Exception {
        String payload = "{\"id\":123,\"type\":\"payment\"}";
        String signatureWithWrongSecret = computeHmacSha256(payload, "wrong-secret");

        boolean result = validator.isValid(payload.getBytes(StandardCharsets.UTF_8), signatureWithWrongSecret);

        assertFalse(result, "A signature computed with a different secret must be rejected");
    }

    /**
     * Helper: computes the expected HMAC-SHA256 hex signature.
     * This mirrors what the caller would compute.
     */
    private static String computeHmacSha256(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hmacBytes);
    }
}
