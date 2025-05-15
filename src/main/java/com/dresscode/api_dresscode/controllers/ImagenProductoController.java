package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.ImagenProducto;
import com.dresscode.api_dresscode.services.ImagenProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imagenes-producto")
@RequiredArgsConstructor
public class ImagenProductoController {
    private final ImagenProductoService imagenProductoService;

    @GetMapping
    public ResponseEntity<?> getAllImagenes() {
        try {
            List<ImagenProducto> imagenes = imagenProductoService.getAllImagenes();
            return ResponseEntity.ok(imagenes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/{imagenId}")
    public ResponseEntity<?> getImagenById(@PathVariable Long id) {
        try {
            ImagenProducto imagen = imagenProductoService.getImagenById(id);
            return ResponseEntity.ok(imagen);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> getImagenesByProductoId(@PathVariable Long productoId) {
        try {
            List<ImagenProducto> imagenes = imagenProductoService.getImagenesByProductoId(productoId);
            return ResponseEntity.ok(imagenes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/producto/{productoId}")
    public ResponseEntity<?> createImagen(@PathVariable Long productoId, @RequestBody ImagenProducto imagenProducto) {
        try {
            ImagenProducto nuevaImagen = imagenProductoService.createImagen(productoId, imagenProducto);
            return ResponseEntity.ok(nuevaImagen);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{imagenId}")
    public ResponseEntity<?> updateImagen(@PathVariable Long id, @RequestBody ImagenProducto imagenActualizada) {
        try {
            ImagenProducto imagen = imagenProductoService.updateImagen(id, imagenActualizada);
            return ResponseEntity.ok(imagen);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{imagenId}")
    public ResponseEntity<?> deleteImagen(@PathVariable Long id) {
        try {
            imagenProductoService.deleteImagen(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
