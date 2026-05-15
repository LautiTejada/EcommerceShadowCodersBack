package com.dresscode.api_dresscode.Auth;

import com.dresscode.api_dresscode.Jwt.JwtService;
import com.dresscode.api_dresscode.dtos.ForgotPasswordRequest;
import com.dresscode.api_dresscode.dtos.ResetPasswordRequest;
import com.dresscode.api_dresscode.entities.PasswordResetToken;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.entities.Usuario.Rol;
import com.dresscode.api_dresscode.repositories.PasswordResetTokenRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import com.dresscode.api_dresscode.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        Usuario user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + request.getUsername()));

        String token = jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .id(user.getId())
                .email(user.getEmail())
                .rol(user.getRol().name())
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        Usuario.Rol rol = request.getRol() != null ? request.getRol() : Usuario.Rol.USER;
        if (rol != Usuario.Rol.USER && rol != Usuario.Rol.ADMIN) {
            throw new RuntimeException("Rol inválido");
        }
        Usuario user = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(rol)
                .build();

        userRepository.save(user);
        // TODO: habilitar cuando tengamos email configurado
        // emailService.enviarBienvenida(user.getEmail(), user.getUsername());
        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .rol(user.getRol().name())
                .build();
    }

    /**
     * Genera un token de recuperación, lo persiste y envía el email.
     * Siempre responde con el mismo mensaje para no revelar si el email existe.
     */
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            passwordResetTokenRepository.deleteByUsuarioId(user.getId());
            String raw = UUID.randomUUID().toString();
            PasswordResetToken prt = PasswordResetToken.builder()
                    .token(raw)
                    .usuario(user)
                    .expiracion(LocalDateTime.now().plusHours(1))
                    .build();
            passwordResetTokenRepository.save(prt);
            // TODO: habilitar cuando tengamos email configurado
            // emailService.enviarRecuperacionPassword(user.getEmail(), user.getUsername(), raw);
        });
    }

    /**
     * Valida el token y actualiza la contraseña.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken prt = passwordResetTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido o inexistente"));

        if (prt.isUsado())    throw new RuntimeException("El token ya fue utilizado");
        if (prt.isExpirado()) throw new RuntimeException("El token ha expirado");

        Usuario user = prt.getUsuario();
        user.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        userRepository.save(user);

        prt.setUsado(true);
        passwordResetTokenRepository.save(prt);
    }
}