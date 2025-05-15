package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.entities.Usuario;
import com.dresscode.api_dresscode.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor

public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.gertAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioService.getUsuarioById(usuarioId);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.createUsuario(usuario);
        return ResponseEntity.status(201).body(nuevoUsuario);
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long usuarioId, @Valid @RequestBody Usuario usuarioActualizado) {
        Usuario usuario = usuarioService.updateUsuario(usuarioId, usuarioActualizado);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long usuarioId) {
        usuarioService.deleteUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{usuarioId}/direcciones")
    public ResponseEntity<Direccion> crearDireccionYAsignarAUsuario(
            @PathVariable Long usuarioId,
            @Valid @RequestBody Direccion direccion) {

        Direccion nuevaDireccion = usuarioService.crearDireccionYAsignarAUsuario(usuarioId, direccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaDireccion);
    }

    @GetMapping("/{usuarioId}/direcciones")
    public ResponseEntity<List<Direccion>> obtenerDireccionesDeUsuario(@PathVariable Long usuarioId) {
        List<Direccion> direcciones = usuarioService.obtenerDireccionesDeUsuario(usuarioId);
        return ResponseEntity.ok(direcciones);
    }

    @PutMapping("/{usuarioId}/direcciones/{direccionId}")
    public ResponseEntity<Direccion> editarDireccionDeUsuario(
            @PathVariable Long usuarioId,
            @PathVariable Long direccionId,
            @Valid @RequestBody Direccion direccionActualizada) {

        Direccion direccionEditada = usuarioService.editarDireccionDeUsuario(usuarioId, direccionId, direccionActualizada);
        return ResponseEntity.ok(direccionEditada);
    }




}
