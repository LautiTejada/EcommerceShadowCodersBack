package com.dresscode.api_dresscode.services;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.repositories.BaseRepository;
import com.dresscode.api_dresscode.repositories.DireccionRepository;
import com.dresscode.api_dresscode.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
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
    protected BaseRepository<Usuario, Long> getRepository() {return usuarioRepository;}



    @Override
    public Usuario save(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está en uso.");
        }
        if (usuario.getRol() == null ||
                (!usuario.getRol().equals(Usuario.Rol.ADMIN) && !usuario.getRol().equals(Usuario.Rol.USER))) {
            throw new RuntimeException("Rol inválido. Debe ser ADMIN o USER.");
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
            // If the password looks already encoded (starts with {algo} prefix or $2a$ legacy), preserve it.
            // Otherwise encode it. Using DelegatingPasswordEncoder, new hashes start with {bcrypt}.
            if (!isAlreadyEncoded(nuevaPassword)) {
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
        // Uses repository query to avoid LazyInitializationException outside a @Transactional boundary.
        // Do NOT call usuario.getDirecciones() here — the collection is now LAZY.
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado con id: " + usuarioId);
        }
        return direccionRepository.findByUsuarioId(usuarioId);
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

    /**
     * Verifica si el usuario autenticado (por username) es el propietario de un usuario específico
     * @param usuarioId ID del usuario a verificar
     * @param username Username del usuario autenticado
     * @return true si el usuario autenticado es el propietario o es ADMIN
     */
    public boolean esElMismoUsuario(Long usuarioId, String username) {
        Usuario usuarioAutenticado = usuarioRepository.findByUsername(username).orElse(null);
        if (usuarioAutenticado == null) {
            return false;
        }
        return usuarioAutenticado.getId().equals(usuarioId);
    }
    /**
     * Checks if a password string appears to be already encoded.
     * Handles both DelegatingPasswordEncoder format ({bcrypt}$2a$...) and
     * legacy bare bcrypt format ($2a$...).
     * Pure function — no side effects.
     */
    static boolean isAlreadyEncoded(String password) {
        if (password == null) return false;
        // DelegatingPasswordEncoder format: {id}hash
        if (password.startsWith("{") && password.contains("}")) return true;
        // Legacy bare bcrypt
        if (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) return true;
        return false;
    }

    /**
     * Obtiene el ID de un usuario por su username
     * @param username Username del usuario
     * @return ID del usuario
     */
    public Long obtenerIdPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getId();
    }

    /**
     * Assigns a role to a target user, enforcing privilege ordering.
     *
     * Rules:
     * - Only ADMIN callers may assign roles.
     * - A caller may only assign roles with strictly lower privilege than their own.
     * - Privilege order: ADMIN (highest, level 0) > USER (lowest, level 1).
     * - This means ADMIN may only assign USER (level 1 > level 0), never ADMIN (same level).
     *
     * @param targetUserId ID of the user to receive the new role
     * @param newRole      The role to assign
     * @param caller       The authenticated user performing the assignment
     * @throws SecurityException if the caller lacks permission to assign the target role
     */
    public void assignRole(Long targetUserId, Usuario.Rol newRole, Usuario caller) {
        if (caller.getRol() != Usuario.Rol.ADMIN) {
            throw new SecurityException(
                    "Solo los administradores pueden asignar roles.");
        }

        int callerLevel = privilegeLevel(caller.getRol());
        int targetLevel = privilegeLevel(newRole);

        if (targetLevel <= callerLevel) {
            // targetLevel must be STRICTLY GREATER than callerLevel (lower privilege)
            // ADMIN is level 0, USER is level 1. ADMIN may only assign roles with level > 0.
            throw new SecurityException(
                    "No podés asignar un rol de igual o mayor privilegio que el tuyo.");
        }

        Usuario targetUser = findById(targetUserId);
        targetUser.setRol(newRole);
        usuarioRepository.save(targetUser);
    }

    /**
     * Returns the privilege level of a role.
     * Lower number = higher privilege.
     * Pure function — deterministic, no side effects.
     *
     * @param rol the role to evaluate
     * @return privilege level (0 = highest)
     */
    static int privilegeLevel(Usuario.Rol rol) {
        return switch (rol) {
            case ADMIN -> 0;
            case USER -> 1;
        };
    }
}
