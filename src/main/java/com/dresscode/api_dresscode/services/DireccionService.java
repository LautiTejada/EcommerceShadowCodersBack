package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.entities.UsuarioDireccion;
import com.dresscode.api_dresscode.repositories.DireccionRepository;
import com.dresscode.api_dresscode.repositories.UsuarioDireccionRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class DireccionService {

    private DireccionRepository direccionRepository;

    private final UsuarioRepository usuarioRepository;

    private final UsuarioDireccionRepository usuarioDireccionRepository;

    public Direccion getDireccionById(Long id) {
        return direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con id: "+ id));
    }

    @Transactional
    public Direccion crearDireccion(Long usuarioId, Direccion direccion, String tipoDireccion) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));

        Direccion direccionGuardada = direccionRepository.save(direccion);

        UsuarioDireccion usuarioDireccion = UsuarioDireccion.builder()
                .usuario(usuario)
                .direccion(direccionGuardada)
                .tipoDireccion(tipoDireccion)
                .build();

        usuarioDireccionRepository.save(usuarioDireccion);

        return direccionGuardada;
    }

    @Transactional
    public Direccion editarDireccion(Long direccionId, Direccion datosActualizados) {
        Direccion direccionExistente = direccionRepository.findById(direccionId)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con id: " + direccionId));

        direccionExistente.setCalle(datosActualizados.getCalle());
        direccionExistente.setNumero(datosActualizados.getNumero());
        direccionExistente.setCodigoPostal(datosActualizados.getCodigoPostal());
        direccionExistente.setLocalidad(datosActualizados.getLocalidad());
        direccionExistente.setProvincia(datosActualizados.getProvincia());
        direccionExistente.setPais(datosActualizados.getPais());

        return direccionRepository.save(direccionExistente);
    }

    @Transactional
    public void eliminarDireccion(Long direccionId) {
        Direccion direccionExistente = direccionRepository.findById(direccionId)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con id: " + direccionId));

        // Eliminar relaciones en usuario_direccion
        List<UsuarioDireccion> relaciones = usuarioDireccionRepository.findAll()
                .stream()
                .filter(ud -> ud.getDireccion().getId().equals(direccionId))
                .toList();

        usuarioDireccionRepository.deleteAll(relaciones);

        // Eliminar dirección
        direccionRepository.delete(direccionExistente);
    }

    public List<Direccion> getDireccionesByUsuario(Long usuarioId) {
        return usuarioDireccionRepository.findDireccionesByUsuarioId(usuarioId);
    }
}
