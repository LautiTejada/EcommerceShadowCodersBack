package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.DireccionRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class UsuarioService extends BaseServiceImpl<Usuario, Long>{

    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final DireccionService direccionService;

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
        usuarioExistente.setPassword(usuarioActualizado.getPassword());
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

}
