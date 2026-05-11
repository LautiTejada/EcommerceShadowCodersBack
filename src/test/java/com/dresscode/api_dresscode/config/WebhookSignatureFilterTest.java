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
 * Integration tests for WebhookSignatureFilter — MercadoPago HMAC-SHA256 scheme.
 *
 * MP sends:
 *   POST /webhook?id={paymentId}&topic=payment
 *   Headers: x-signature: ts=<ts>,v1=<hmac>
 *            x-request-id: <requestId>
 *
 * The filter computes: HMAC("id:{id};request-id:{x-request-id};ts:{ts}")
 * and rejects with 401 if invalid or missing.
 *
 * Test profile uses mercadopago.webhook-secret=test-hmac-secret-for-tests
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebhookSignatureFilterTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_SECRET = "test-hmac-secret-for-tests";
    private static final String WEBHOOK_PATH = "/api/mercado-pago/webhook";
    private static final String PAYMENT_ID = "12345";
    private static final String REQUEST_ID = "req-test-001";
    private static final String TS = "1704720092";

    @Test
    void webhookWithMissingSignature_returns401() throws Exception {
        mockMvc.perform(post(WEBHOOK_PATH)
                .param("id", PAYMENT_ID)
                .param("topic", "payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void webhookWithInvalidSignature_returns401() throws Exception {
        mockMvc.perform(post(WEBHOOK_PATH)
                .param("id", PAYMENT_ID)
                .param("topic", "payment")
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-signature", "ts=" + TS + ",v1=deadbeef00000000")
                .header("x-request-id", REQUEST_ID)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void webhookWithValidSignature_isNotRejectedWith401() throws Exception {
        String xSignature = buildXSignature(PAYMENT_ID, REQUEST_ID, TS, TEST_SECRET);

        mockMvc.perform(post(WEBHOOK_PATH)
                .param("id", PAYMENT_ID)
                .param("topic", "payment")
                .contentType(MediaType.APPLICATION_JSON)
                .header("x-signature", xSignature)
                .header("x-request-id", REQUEST_ID)
                .content("{}"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401) {
                        throw new AssertionError(
                                "Webhook with valid MP signature must NOT return 401, got " + status);
                    }
                });
    }

    private static String buildXSignature(String dataId, String requestId, String ts, String secret) throws Exception {
        String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String hmac = HexFormat.of().formatHex(mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8)));
        return "ts=" + ts + ",v1=" + hmac;
    }
}
