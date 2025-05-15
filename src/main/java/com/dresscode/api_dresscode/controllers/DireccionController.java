package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.services.DireccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/direcciones")
@RequiredArgsConstructor
public class DireccionController {
 private final DireccionService direccionService;

 @GetMapping("/{direccionId}")
 public ResponseEntity<?> obtenerDireccionPorId(@PathVariable Long direccionId) {
     try {
         Direccion direccion = direccionService.getDireccionById(direccionId);
         return ResponseEntity.ok(direccion);
     } catch (RuntimeException e) {
         return ResponseEntity.badRequest().body(e.getMessage());
     }
 }

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> crearDireccion(
            @PathVariable Long usuarioId,
            @RequestBody Direccion direccion,
            @RequestParam String tipoDireccion) {
        try {
            Direccion nuevaDireccion = direccionService.crearDireccion(usuarioId, direccion, tipoDireccion);
            return ResponseEntity.ok(nuevaDireccion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{direccionId}")
    public ResponseEntity<?> editarDireccion(
            @PathVariable Long direccionId,
            @RequestBody Direccion datosActualizados) {
        try {
            Direccion direccionActualizada = direccionService.editarDireccion(direccionId, datosActualizados);
            return ResponseEntity.ok(direccionActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{direccionId}")
    public ResponseEntity<?> eliminarDireccion(@PathVariable Long direccionId) {
        try {
            direccionService.eliminarDireccion(direccionId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerDireccionesPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<Direccion> direcciones = direccionService.getDireccionesByUsuario(usuarioId);
            return ResponseEntity.ok(direcciones);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
