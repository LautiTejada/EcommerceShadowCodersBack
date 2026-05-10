package com.dresscode.api_dresscode.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Validates HMAC-SHA256 webhook signatures.
 *
 * SECURITY CONTRACT:
 * - Comparison uses MessageDigest.isEqual (timing-safe) — NOT String.equals.
 *   String.equals is vulnerable to timing attacks that leak information about
 *   how many bytes match, allowing an attacker to forge signatures incrementally.
 * - The secret is injected from `webhook.hmac-secret` (externalized, never hardcoded).
 *
 * Usage: inject this bean and call isValid(payloadBytes, receivedSignature).
 */
@Component
public class WebhookSignatureValidator {

    private final String hmacSecret;

    public WebhookSignatureValidator(@Value("${webhook.hmac-secret}") String hmacSecret) {
        this.hmacSecret = hmacSecret;
    }

    /**
     * Returns true if the received signature is a valid HMAC-SHA256 of the payload
     * using the externalized secret.
     *
     * Pure-logic side: computing HMAC is deterministic for the same inputs.
     *
     * @param payload           raw request body bytes
     * @param receivedSignature hex-encoded HMAC-SHA256 provided by the caller
     * @return true iff the signature matches
     */
    public boolean isValid(byte[] payload, String receivedSignature) {
        if (receivedSignature == null || receivedSignature.isBlank()) {
            return false;
        }

        try {
            byte[] expectedHmac = computeHmac(payload);
            byte[] receivedHmac = HexFormat.of().parseHex(receivedSignature.trim());

            // MessageDigest.isEqual is timing-safe — it always compares all bytes
            // regardless of where the first mismatch occurs.
            return MessageDigest.isEqual(expectedHmac, receivedHmac);
        } catch (IllegalArgumentException e) {
            // receivedSignature was not valid hex
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Computes HMAC-SHA256 for the given payload using the injected secret.
     */
    private byte[] computeHmac(byte[] payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(
                hmacSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        return mac.doFinal(payload);
    }
}
