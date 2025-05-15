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

    @GetMapping
    public ResponseEntity<List<Direccion>> obtenerTodasLasDirecciones(){
        List<Direccion> direcciones = direccionService.gertAllDirecciones();
        return ResponseEntity.ok(direcciones);
    }

     @GetMapping("/{direccionId}")
     public ResponseEntity<Direccion> obtenerDireccionPorId(@PathVariable Long direccionId) {
             Direccion direccion = direccionService.getDireccionById(direccionId);
             return ResponseEntity.ok(direccion);
     }

    @PutMapping("/{direccionId}")
    public ResponseEntity<Direccion> editarDireccion(
            @PathVariable Long direccionId,
            @RequestBody Direccion datosActualizados) {
            Direccion direccionActualizada = direccionService.editarDireccion(direccionId, datosActualizados);
            return ResponseEntity.ok(direccionActualizada);

    }

    @DeleteMapping("/{direccionId}")
    public ResponseEntity<Direccion> eliminarDireccion(@PathVariable Long direccionId) {
            direccionService.eliminarDireccion(direccionId);
            return ResponseEntity.noContent().build();

    }
}
