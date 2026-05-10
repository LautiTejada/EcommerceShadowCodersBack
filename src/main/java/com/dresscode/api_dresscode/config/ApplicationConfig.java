package com.dresscode.api_dresscode.config;

import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApplicationConfig {
    private final UsuarioRepository userRepository;

    public ApplicationConfig(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));
    }

    /**
     * Password encoder using DelegatingPasswordEncoder defaulting to {bcrypt}.
     *
     * DESIGN DECISION:
     * - DelegatingPasswordEncoder stores the encoding ID in the password string: {bcrypt}$2a$10$...
     * - This allows future algorithm upgrades without breaking existing passwords.
     * - Legacy passwords stored as bare $2a$10$... (no prefix) are handled by the bcrypt
     *   fallback encoder for matching — they will NOT be auto-upgraded on match.
     * - Newly encoded passwords always use {bcrypt} prefix.
     *
     * MIGRATION NOTE: existing passwords stored without the {bcrypt} prefix should be
     * re-encoded on next successful login (upgrade-on-login pattern). Until then,
     * the fallback encoder handles them.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        String defaultEncodingId = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(defaultEncodingId, new BCryptPasswordEncoder());

        DelegatingPasswordEncoder delegating = new DelegatingPasswordEncoder(defaultEncodingId, encoders);
        // Fallback for passwords stored without an encoding prefix (legacy bare $2a$ bcrypt hashes)
        delegating.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        return delegating;
    }
}
