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
    public ResponseEntity<List<ImagenProducto>> getAllImagenes() {
            List<ImagenProducto> imagenes = imagenProductoService.getAllImagenes();
            return ResponseEntity.ok(imagenes);
    }

    @GetMapping("/{imagenId}")
    public ResponseEntity<ImagenProducto> getImagenById(@PathVariable Long imagenId) {
        ImagenProducto imagen = imagenProductoService.getImagenById(imagenId);
        return ResponseEntity.ok(imagen);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ImagenProducto>> getImagenesByProductoId(@PathVariable Long productoId) {
        List<ImagenProducto> imagenes = imagenProductoService.getImagenesByProductoId(productoId);
        return ResponseEntity.ok(imagenes);
    }

    @PostMapping("/producto/{productoId}")
    public ResponseEntity<ImagenProducto> createImagen(@PathVariable Long productoId, @RequestBody ImagenProducto imagenProducto) {
        ImagenProducto nuevaImagen = imagenProductoService.createImagen(productoId, imagenProducto);
        return ResponseEntity.ok(nuevaImagen);
    }

    @PutMapping("/{imagenId}")
    public ResponseEntity<ImagenProducto> updateImagen(@PathVariable Long imagenId, @RequestBody ImagenProducto imagenActualizada) {
        ImagenProducto imagen = imagenProductoService.updateImagen(imagenId, imagenActualizada);
        return ResponseEntity.ok(imagen);
    }

    @DeleteMapping("/{imagenId}")
    public ResponseEntity<ImagenProducto> deleteImagen(@PathVariable Long imagenId) {
        imagenProductoService.deleteImagen(imagenId);
        return ResponseEntity.ok().build();
    }
}
