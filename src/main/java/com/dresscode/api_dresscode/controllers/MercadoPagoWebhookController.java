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

/**
 * Receives MercadoPago payment notifications (webhooks).
 *
 * Signature validation is handled upstream by WebhookSignatureFilter —
 * by the time a request reaches this controller, it is already authenticated.
 *
 * MP calls this endpoint as:
 *   POST /api/mercado-pago/webhook?id={paymentId}&topic=payment
 */
@RestController
@RequestMapping("/api/mercado-pago")
@RequiredArgsConstructor
public class MercadoPagoWebhookController {

    private final OrdenDeCompraService ordenDeCompraService;
    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoWebhookController.class);

    @Value("${mercadopago.access-token}")
    private String mercadoPagoAccessToken;

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(
            @RequestParam("id") String paymentId,
            @RequestParam("topic") String topic) {

        if (!"payment".equals(topic)) {
            logger.info("Webhook topic '{}' ignored", topic);
            return ResponseEntity.ok("Topic ignored");
        }

        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);

        try {
            Long id = Long.parseLong(paymentId);
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(id);
            ordenDeCompraService.actualizarEstadoPorPago(payment);
            logger.info("Webhook procesado: paymentId={}, status={}", paymentId, payment.getStatus());
            return ResponseEntity.ok("OK");
        } catch (NumberFormatException e) {
            logger.error("paymentId inválido: {}", paymentId);
            return ResponseEntity.badRequest().body("paymentId inválido");
        } catch (Exception e) {
            logger.error("Error procesando webhook: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
