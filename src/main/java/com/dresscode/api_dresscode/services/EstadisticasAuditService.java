package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.EstadisticasAudit;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.EstadisticasAuditRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadisticasAuditService {

    private final EstadisticasAuditRepository estadisticasAuditRepository;
    private final UsuarioRepository usuarioRepository;

    public void registrarAcceso(String username, String accion, String endpoint, String detalles) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para auditoria: " + username));

        EstadisticasAudit audit = EstadisticasAudit.builder()
                .usuario(usuario)
                .accion(accion)
                .endpoint(endpoint)
                .fechaAcceso(LocalDateTime.now())
                .detalles(detalles)
                .build();

        estadisticasAuditRepository.save(audit);
    }

    public List<EstadisticasAudit> obtenerAuditorias() {
        return estadisticasAuditRepository.findAll();
    }
}
