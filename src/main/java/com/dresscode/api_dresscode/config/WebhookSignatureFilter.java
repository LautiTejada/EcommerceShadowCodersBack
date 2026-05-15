package com.dresscode.api_dresscode.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * Validates MercadoPago webhook signatures on all /api/mercado-pago/webhook requests.
 *
 * MP sends the signature in the "x-signature" header with the format:
 *   ts=<timestamp>,v1=<hmac-hex>
 *
 * The signed manifest is: id:{data.id};request-id:{x-request-id};ts:{ts}
 * where data.id comes from the ?id= query param and x-request-id from the header.
 *
 * Requests without a valid signature are rejected with HTTP 401.
 */
@Component
@RequiredArgsConstructor
public class WebhookSignatureFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(WebhookSignatureFilter.class);
    private static final String WEBHOOK_PATH = "/api/mercado-pago/webhook";

    private final WebhookSignatureValidator signatureValidator;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!isWebhookPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Wrap so the body can be re-read by the downstream controller
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        // MP sends the payment id as ?id= query param
        String dataId = wrappedRequest.getParameter("id");
        // MP sends its own request id in this header
        String requestId = wrappedRequest.getHeader("x-request-id");
        // MP sends the signature in this header: ts=...,v1=...
        String xSignature = wrappedRequest.getHeader("x-signature");

        if (dataId == null || dataId.isBlank()) {
            log.warn("Webhook request missing ?id= param");
            reject(response, "Missing id param");
            return;
        }

        if (!signatureValidator.isValid(dataId, requestId != null ? requestId : "", xSignature)) {
            log.warn("Webhook request has invalid x-signature");
            reject(response, "Invalid webhook signature");
            return;
        }

        filterChain.doFilter(wrappedRequest, response);
    }

    private boolean isWebhookPath(String requestUri) {
        return pathMatcher.match(WEBHOOK_PATH, requestUri)
                || requestUri.startsWith(WEBHOOK_PATH);
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
