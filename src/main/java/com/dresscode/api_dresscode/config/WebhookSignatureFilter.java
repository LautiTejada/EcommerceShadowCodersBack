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
 * Filter that validates HMAC-SHA256 webhook signatures on all /api/mercado-pago/webhook requests.
 *
 * SECURITY CONTRACT:
 * - Requests to the webhook path WITHOUT a valid X-Hub-Signature-256 header → HTTP 401.
 * - Signature validation uses timing-safe comparison (via WebhookSignatureValidator).
 * - The raw request body is cached via ContentCachingRequestWrapper so it can be read
 *   both by this filter AND by the downstream controller (HttpServletRequest is not repeatable by default).
 *
 * This filter is registered BEFORE Spring Security's JwtAuthenticationFilter
 * because webhook endpoints do not carry JWT tokens — they use HMAC signatures instead.
 */
@Component
@RequiredArgsConstructor
public class WebhookSignatureFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(WebhookSignatureFilter.class);
    private static final String WEBHOOK_PATH = "/api/mercado-pago/webhook";
    private static final String SIGNATURE_HEADER = "X-Hub-Signature-256";

    private final WebhookSignatureValidator signatureValidator;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Only guard the webhook endpoint; pass everything else through
        if (!isWebhookPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Wrap to allow body re-reading downstream
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        String receivedSignature = wrappedRequest.getHeader(SIGNATURE_HEADER);
        if (receivedSignature == null || receivedSignature.isBlank()) {
            log.warn("Webhook request missing {}", SIGNATURE_HEADER);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Missing webhook signature\"}");
            return;
        }

        // Force the body to be consumed so ContentCachingRequestWrapper caches it
        byte[] body = wrappedRequest.getInputStream().readAllBytes();

        if (!signatureValidator.isValid(body, receivedSignature)) {
            log.warn("Webhook request has invalid {}", SIGNATURE_HEADER);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid webhook signature\"}");
            return;
        }

        filterChain.doFilter(wrappedRequest, response);
    }

    private boolean isWebhookPath(String requestUri) {
        return pathMatcher.match(WEBHOOK_PATH, requestUri)
                || requestUri.startsWith(WEBHOOK_PATH);
    }
}
