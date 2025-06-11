package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.DireccionRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class UsuarioService extends BaseServiceImpl<Usuario, Long>{

    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final DireccionService direccionService;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected JpaRepository<Usuario, Long> getRepository() {return usuarioRepository;}



    @Override
    public Usuario save(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está en uso.");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario update(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = findById(id);
        usuarioExistente.setUsername(usuarioActualizado.getUsername());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        String nuevaPassword = usuarioActualizado.getPassword();
        if (nuevaPassword != null && !nuevaPassword.isBlank() &&
                !nuevaPassword.equals(usuarioExistente.getPassword())) {
            // Si la nueva contraseña no está hasheada, la hasheamos
            if (!nuevaPassword.startsWith("$2a$")) {
                usuarioExistente.setPassword(passwordEncoder.encode(nuevaPassword));
            } else {
                usuarioExistente.setPassword(nuevaPassword);
            }
        }

        usuarioExistente.setRol(usuarioActualizado.getRol());
        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public Direccion crearDireccionYAsignarAUsuario(Long usuarioId, Direccion direccion) {

        Usuario usuario = findById(usuarioId);

        direccion.setUsuario(usuario);

        Direccion nuevaDireccion = direccionRepository.save(direccion);

        usuario.getDirecciones().add(nuevaDireccion);
        usuarioRepository.save(usuario);

        return nuevaDireccion;
    }

    public List<Direccion> obtenerDireccionesDeUsuario(Long usuarioId) {
        Usuario usuario = findById(usuarioId);
        return usuario.getDirecciones();
    }


    @Transactional
    public Direccion editarDireccionDeUsuario(Long usuarioId, Long direccionId, Direccion direccionActualizada) {
        Usuario usuario = findById(usuarioId);

        Direccion direccion = direccionService.findById(direccionId);

        direccion.setCalle(direccionActualizada.getCalle());
        direccion.setNumero(direccionActualizada.getNumero());
        direccion.setCodigoPostal(direccionActualizada.getCodigoPostal());
        direccion.setLocalidad(direccionActualizada.getLocalidad());
        direccion.setProvincia(direccionActualizada.getProvincia());

        return direccionRepository.save(direccion);
    }
    @Transactional
    public void desactivarDireccion(Long usuarioId, Long direccionId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Direccion direccion = direccionRepository.findById(direccionId)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        if (!usuario.getDirecciones().contains(direccion)) {
            throw new RuntimeException("La dirección no pertenece al usuario");
        }

        direccion.setActivo(false);
        direccionRepository.save(direccion);
    }

}
