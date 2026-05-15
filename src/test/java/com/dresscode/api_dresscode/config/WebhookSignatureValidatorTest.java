package com.dresscode.api_dresscode.config;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WebhookSignatureValidator — MercadoPago HMAC-SHA256 scheme.
 *
 * MP signs the manifest: "id:{dataId};request-id:{requestId};ts:{ts}"
 * and sends the signature as: "ts=<ts>,v1=<hex-hmac>" in the x-signature header.
 */
class WebhookSignatureValidatorTest {

    private static final String SECRET = "test-secret-key";
    private final WebhookSignatureValidator validator = new WebhookSignatureValidator(SECRET);

    @Test
    void validSignature_returnsTrue() throws Exception {
        String dataId = "123";
        String requestId = "req-abc";
        String ts = "1704720092";
        String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts;
        String hmac = computeHmac(manifest, SECRET);
        String xSignature = "ts=" + ts + ",v1=" + hmac;

        assertTrue(validator.isValid(dataId, requestId, xSignature),
                "A correctly computed MP signature must be accepted");
    }

    @Test
    void tamperedDataId_returnsFalse() throws Exception {
        String requestId = "req-abc";
        String ts = "1704720092";
        String manifest = "id:123;request-id:" + requestId + ";ts:" + ts;
        String hmac = computeHmac(manifest, SECRET);
        String xSignature = "ts=" + ts + ",v1=" + hmac;

        assertFalse(validator.isValid("999", requestId, xSignature),
                "A signature for a different id must be rejected");
    }

    @Test
    void nullSignature_returnsFalse() {
        assertFalse(validator.isValid("123", "req-abc", null),
                "A null x-signature must be rejected");
    }

    @Test
    void blankSignature_returnsFalse() {
        assertFalse(validator.isValid("123", "req-abc", "   "),
                "A blank x-signature must be rejected");
    }

    @Test
    void wrongSecret_returnsFalse() throws Exception {
        String dataId = "123";
        String requestId = "req-abc";
        String ts = "1704720092";
        String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts;
        String hmac = computeHmac(manifest, "wrong-secret");
        String xSignature = "ts=" + ts + ",v1=" + hmac;

        assertFalse(validator.isValid(dataId, requestId, xSignature),
                "A signature computed with a different secret must be rejected");
    }

    @Test
    void missingV1Field_returnsFalse() {
        assertFalse(validator.isValid("123", "req-abc", "ts=1704720092"),
                "x-signature without v1 field must be rejected");
    }

    @Test
    void noSecretConfigured_returnsTrue() {
        // Dev/CI: blank secret skips validation
        WebhookSignatureValidator devValidator = new WebhookSignatureValidator("");
        assertTrue(devValidator.isValid("123", "req-abc", null),
                "When no secret is configured, validation is skipped and returns true");
    }

    private static String computeHmac(String manifest, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8)));
    }
}
