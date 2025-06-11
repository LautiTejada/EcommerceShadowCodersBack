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

public class UsuarioController extends BaseController<Usuario, Long>{

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        super(usuarioService);
        this.usuarioService = usuarioService;
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
    @PutMapping("/{usuarioId}/direcciones/{direccionId}/desactivar")
    public ResponseEntity<?> desactivarDireccionDeUsuario(
            @PathVariable Long usuarioId,
            @PathVariable Long direccionId) {
        usuarioService.desactivarDireccion(usuarioId, direccionId);
        return ResponseEntity.ok().build();
    }




}
