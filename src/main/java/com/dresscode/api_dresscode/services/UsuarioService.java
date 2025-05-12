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

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioDireccionRepository usuarioDireccionRepository;
    private final DireccionRepository direccionRepository;

    public List<Usuario> gertAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario getUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    public Usuario createUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está en uso.");
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario updateUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = getUsuarioById(id);
        usuarioExistente.setNombreUsuario(usuarioActualizado.getNombreUsuario());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setContrasena(usuarioActualizado.getContrasena());
        usuarioExistente.setRol(usuarioActualizado.getRol());
        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void deleteUsuario(Long id) {
        Usuario usuarioEliminado = getUsuarioById(id);
        usuarioRepository.delete(usuarioEliminado);
    }

    @Transactional
    public UsuarioDireccion crearDireccionYAsignarAUsuario(Long usuarioId, Direccion direccion) {
        Usuario usuario = getUsuarioById(usuarioId);

        Direccion nuevaDireccion = direccionRepository.save(direccion);

        UsuarioDireccion usuarioDireccion = UsuarioDireccion.builder()
                .usuario(usuario)
                .direccion(nuevaDireccion)
                .build();

        UsuarioDireccion guardada = usuarioDireccionRepository.save(usuarioDireccion);


        usuario.getDirecciones().add(guardada);
        usuarioRepository.save(usuario);

        return guardada;
    }

    @Transactional(readOnly = true)
    public List<Direccion> obtenerDireccionesDeUsuario(Long usuarioId) {
        Usuario usuario = getUsuarioById(usuarioId);

        return usuario.getDirecciones().stream()
                .map(UsuarioDireccion::getDireccion)
                .toList();
    }
}
