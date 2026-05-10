package com.dresscode.api_dresscode.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration.
 *
 * SECURITY RULES:
 * - Allowed origins come exclusively from `cors.allowed-origins` property (externalized).
 * - Wildcard '*' is NEVER used — not as origin, not as a fallback.
 * - If the property is empty or blank, NO origins are allowed (fail-closed).
 * - @CrossOrigin annotations are PROHIBITED on controllers; this bean is the single source of truth.
 */
@Configuration
public class CorsConfig {

    /**
     * Comma-separated list of allowed origins, e.g. "https://app.example.com,https://admin.example.com".
     * Resolved from the `cors.allowed-origins` environment property.
     */
    @Value("${cors.allowed-origins:}")
    private String allowedOriginsRaw;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = parseOrigins(allowedOriginsRaw);
        if (origins.isEmpty()) {
            // Fail-closed: if no origins are configured, reject all cross-origin requests.
            // This is intentional — misconfiguration should be visible, not silently open.
            configuration.setAllowedOrigins(List.of());
        } else {
            configuration.setAllowedOrigins(origins);
        }

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Parses a comma-separated string of origins into a trimmed list.
     * Empty or blank strings are filtered out.
     * Pure function — deterministic, no side effects.
     */
    static List<String> parseOrigins(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
