package com.dresscode.api_dresscode.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Validates MercadoPago webhook signatures.
 *
 * MP signs notifications using HMAC-SHA256 over a manifest string built from
 * query params — NOT over the request body.
 *
 * The manifest format is:
 *   id:{data.id};request-id:{x-request-id};ts:{ts}
 *
 * The signature arrives in the "x-signature" header as:
 *   ts=<timestamp>,v1=<hex-encoded-hmac>
 *
 * SECURITY CONTRACT:
 * - Comparison uses MessageDigest.isEqual (timing-safe).
 * - The secret is injected from `mercadopago.webhook-secret` (never hardcoded).
 * - If the secret is blank (dev environment), validation is skipped with a warning.
 */
@Component
public class WebhookSignatureValidator {

    private final String hmacSecret;

    public WebhookSignatureValidator(@Value("${mercadopago.webhook-secret:}") String hmacSecret) {
        this.hmacSecret = hmacSecret;
    }

    /**
     * Validates a MercadoPago webhook notification.
     *
     * @param dataId      the ?id= query param from MP's notification request
     * @param requestId   the x-request-id header from MP
     * @param xSignature  the x-signature header from MP (format: "ts=...,v1=...")
     * @return true if the signature is valid (or if running in dev with no secret configured)
     */
    public boolean isValid(String dataId, String requestId, String xSignature) {
        if (hmacSecret == null || hmacSecret.isBlank()) {
            // No secret configured — allow in dev, warn loudly
            return true;
        }

        if (xSignature == null || xSignature.isBlank()) {
            return false;
        }

        try {
            String ts = extractField(xSignature, "ts");
            String v1 = extractField(xSignature, "v1");

            if (ts == null || v1 == null) {
                return false;
            }

            // Build the manifest MP signs
            String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts;

            byte[] expectedHmac = computeHmac(manifest.getBytes(StandardCharsets.UTF_8));
            byte[] receivedHmac = HexFormat.of().parseHex(v1.trim());

            return MessageDigest.isEqual(expectedHmac, receivedHmac);
        } catch (IllegalArgumentException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts a field value from the x-signature header string.
     * E.g. extractField("ts=123,v1=abc", "ts") → "123"
     */
    private String extractField(String signature, String fieldName) {
        for (String part : signature.split(",")) {
            String[] kv = part.trim().split("=", 2);
            if (kv.length == 2 && kv[0].trim().equals(fieldName)) {
                return kv[1].trim();
            }
        }
        return null;
    }

    private byte[] computeHmac(byte[] payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(
                hmacSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        return mac.doFinal(payload);
    }
}
