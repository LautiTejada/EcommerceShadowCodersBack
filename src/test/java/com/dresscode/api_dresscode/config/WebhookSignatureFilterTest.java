package com.dresscode.api_dresscode.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TDD RED: WebhookSignatureFilter integration test.
 *
 * Spec: Req — Webhook Request Integrity Validation
 *   Scenario: Valid signature → request processed (not 401)
 *   Scenario: Missing signature → 401
 *   Scenario: Invalid signature → 401
 *
 * The filter guards /api/mercado-pago/webhook path.
 * Test profile uses webhook.hmac-secret=test-hmac-secret-for-tests
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebhookSignatureFilterTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_SECRET = "test-hmac-secret-for-tests";
    private static final String WEBHOOK_PATH = "/api/mercado-pago/webhook";
    private static final String PAYLOAD = "{\"id\":\"123\",\"topic\":\"payment\"}";

    @Test
    void webhookWithMissingSignature_returns401() throws Exception {
        mockMvc.perform(post(WEBHOOK_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(PAYLOAD))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void webhookWithInvalidSignature_returns401() throws Exception {
        mockMvc.perform(post(WEBHOOK_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Hub-Signature-256", "deadbeef00000000deadbeef00000000")
                .content(PAYLOAD))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void webhookWithValidSignature_isNotRejectedWith401() throws Exception {
        String signature = computeHmacSha256(PAYLOAD, TEST_SECRET);
        mockMvc.perform(post(WEBHOOK_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Hub-Signature-256", signature)
                .content(PAYLOAD))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401) {
                        throw new AssertionError(
                                "Webhook with valid signature must NOT return 401, got " + status);
                    }
                });
    }

    private static String computeHmacSha256(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hmacBytes);
    }
}
