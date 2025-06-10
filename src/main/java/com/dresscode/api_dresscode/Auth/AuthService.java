package com.dresscode.api_dresscode.Auth;

import com.dresscode.api_dresscode.Jwt.JwtService;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;




import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        Usuario user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + request.getEmail()));
        String token = jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .id(user.getId())
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        Usuario user = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Usuario.Rol.USER)
                .build();

        userRepository.save(user);
        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }
}
