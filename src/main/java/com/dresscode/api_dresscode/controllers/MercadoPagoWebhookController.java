package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.services.OrdenDeCompraService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/mercado-pago")
@RequiredArgsConstructor
public class MercadoPagoWebhookController {
    private final OrdenDeCompraService ordenDeCompraService;

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoWebhookController.class);

    @Value("${mercadopago.access-token}")
    private String mercadoPagoAccessToken;

    @Value("${mercadopago.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestParam("id") String paymentId, @RequestParam("topic") String topic, @RequestHeader(value = "X-Webhook-Secret", required = false) String receivedSecret) {
        // Validar clave secreta
        if (webhookSecret != null && !webhookSecret.isEmpty()) {
            if (receivedSecret == null || !webhookSecret.equals(receivedSecret)) {
                logger.warn("Webhook recibido con clave inválida");
                return ResponseEntity.status(401).body("Unauthorized: invalid webhook secret");
            }
        }
        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
        if (!"payment".equals(topic)) {
            return ResponseEntity.badRequest().body("Unsupported topic");
        }
        try {
            Long id = Long.parseLong(paymentId);
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(id);
            String status = payment.getStatus();
            ordenDeCompraService.actualizarEstadoPorPago(payment);
            logger.info("Webhook procesado correctamente: paymentId={}, status={}", paymentId, status);
            return ResponseEntity.ok("Webhook procesado: status=" + status);
        } catch (NumberFormatException e) {
            logger.error("paymentId inválido: {}", paymentId);
            return ResponseEntity.badRequest().body("paymentId inválido");
        } catch (Exception e) {
            logger.error("Error procesando webhook: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error procesando webhook: " + e.getMessage());
        }
    }
}
