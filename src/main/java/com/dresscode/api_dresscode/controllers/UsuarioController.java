package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController extends BaseController<Usuario, Long>{

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        super(usuarioService);
        this.usuarioService = usuarioService;
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAll() throws Exception {
        return super.getAll();
    }

    @Override
    @GetMapping("/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAll(org.springframework.data.domain.Pageable pageable) throws Exception {
        return super.getAll(pageable);
    }

    @Override
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllActive() throws Exception {
        return super.getAllActive();
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esElMismoUsuario(#id, authentication.principal.username)")
    public ResponseEntity<?> getOne(@PathVariable Long id) throws Exception {
        return super.getOne(id);
    }

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> save(@jakarta.validation.Valid @RequestBody Usuario entity) throws Exception {
        return super.save(entity);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esElMismoUsuario(#id, authentication.principal.username)")
    public ResponseEntity<?> update(@PathVariable Long id, @jakarta.validation.Valid @RequestBody Usuario entity) throws Exception {
        return super.update(id, entity);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {
        return super.delete(id);
    }

    @Override
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) throws Exception {
        return super.changeStatus(id);
    }

    @Override
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activate(@PathVariable Long id) throws Exception {
        return super.activate(id);
    }

    @Override
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivate(@PathVariable Long id) throws Exception {
        return super.deactivate(id);
    }

    @PostMapping("/{usuarioId}/direcciones")
    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esElMismoUsuario(#usuarioId, authentication.principal.username)")
    public ResponseEntity<Direccion> crearDireccionYAsignarAUsuario(
            @PathVariable Long usuarioId,
            @Valid @RequestBody Direccion direccion) {

        Direccion nuevaDireccion = usuarioService.crearDireccionYAsignarAUsuario(usuarioId, direccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaDireccion);
    }

    @GetMapping("/{usuarioId}/direcciones")
    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esElMismoUsuario(#usuarioId, authentication.principal.username)")
    public ResponseEntity<List<Direccion>> obtenerDireccionesDeUsuario(@PathVariable Long usuarioId) {
        List<Direccion> direcciones = usuarioService.obtenerDireccionesDeUsuario(usuarioId);
        return ResponseEntity.ok(direcciones);
    }

    @PutMapping("/{usuarioId}/direcciones/{direccionId}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esElMismoUsuario(#usuarioId, authentication.principal.username)")
    public ResponseEntity<Direccion> editarDireccionDeUsuario(
            @PathVariable Long usuarioId,
            @PathVariable Long direccionId,
            @Valid @RequestBody Direccion direccionActualizada) {

        Direccion direccionEditada = usuarioService.editarDireccionDeUsuario(usuarioId, direccionId, direccionActualizada);
        return ResponseEntity.ok(direccionEditada);
    }

    @PutMapping("/{usuarioId}/direcciones/{direccionId}/desactivar")
    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esElMismoUsuario(#usuarioId, authentication.principal.username)")
    public ResponseEntity<?> desactivarDireccionDeUsuario(
            @PathVariable Long usuarioId,
            @PathVariable Long direccionId) {
        usuarioService.desactivarDireccion(usuarioId, direccionId);
        return ResponseEntity.ok().build();
    }

}
