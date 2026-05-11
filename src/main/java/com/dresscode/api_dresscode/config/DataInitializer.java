package com.dresscode.api_dresscode.config;

import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario admin si no existe
        if (!usuarioRepository.existsByEmail("admin@dresscode.com")) {
            Usuario admin = Usuario.builder()
                    .email("admin@dresscode.com")
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Usuario.Rol.ADMIN)
                    .build();
            admin.setActivo(true);
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario ADMIN creado: admin@dresscode.com");
        }

        // Crear usuario regular si no existe
        if (!usuarioRepository.existsByEmail("user@dresscode.com")) {
            Usuario user = Usuario.builder()
                    .email("user@dresscode.com")
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .rol(Usuario.Rol.USER)
                    .build();
            user.setActivo(true);
            usuarioRepository.save(user);
            System.out.println("✅ Usuario USER creado: user@dresscode.com");
        }
    }
}
