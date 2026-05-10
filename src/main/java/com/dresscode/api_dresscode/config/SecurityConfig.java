package com.dresscode.api_dresscode.config;

import com.dresscode.api_dresscode.Jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Security configuration — allow-list approach.
 *
 * DESIGN DECISIONS:
 * - anyRequest().authenticated() replaces anyRequest().permitAll() (security fix)
 * - @EnableMethodSecurity enables @PreAuthorize on all state-mutating endpoints
 * - @Order(1) ensures this chain is evaluated before the default Spring Security chain
 * - CORS is configured via externalized CorsConfigurationSource (no hardcoded origins)
 * - Auth routes (/auth/**) and public resources (/api/banners) are the only permitAll matchers
 * - Swagger/OpenAPI and Actuator health endpoint remain accessible for dev/ops
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Order(1)
public class SecurityConfig {

    private final AuthenticationProvider authProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final WebhookSignatureFilter webhookSignatureFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authRequest ->
                authRequest
                    // Public auth endpoints (AuthController is at /api/auth/**)
                    .requestMatchers("/auth/**", "/api/auth/**").permitAll()
                    // Webhook — signature validated by WebhookSignatureFilter (not JWT)
                    .requestMatchers("/api/mercado-pago/webhook").permitAll()
                    // Public read-only resources
                    .requestMatchers(
                        org.springframework.http.HttpMethod.GET,
                        "/api/banners", "/api/banners/**"
                    ).permitAll()
                    // Swagger / OpenAPI (accessible without auth for API docs)
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                    ).permitAll()
                    // Actuator health (operational readiness probe)
                    .requestMatchers("/actuator/health").permitAll()
                    // Everything else MUST be authenticated — no silent open access
                    .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider)
            .addFilterBefore(webhookSignatureFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
