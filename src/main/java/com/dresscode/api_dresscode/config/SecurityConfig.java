package com.dresscode.api_dresscode.config;


import com.dresscode.api_dresscode.Jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource; // Agregar esta línea
    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authRequest ->
                authRequest
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/api/usuarios/**").hasRole("ADMIN") // Solo ADMIN puede acceder a usuarios
                    .requestMatchers("/api/ordenes-de-compra/**", "/api/detalles-orden/**").authenticated()
                    .requestMatchers("/api/producto-talles/**").authenticated()
                    .requestMatchers("/api/estadisticas/**").hasRole("ADMIN") // Solo ADMIN puede acceder a estadísticas
                    .anyRequest().permitAll()
            )
            .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
        /**
         * Estructura de roles:
         * - ADMIN: Acceso total a endpoints de usuarios (/api/usuarios/**)
         * - USER: Acceso a endpoints autenticados, pero no a gestión de usuarios
         * - Endpoints públicos: /auth/**
         * - Endpoints autenticados: /api/ordenes-de-compra/**, /api/detalles-orden/**, /api/producto-talles/**
         */
    }
}