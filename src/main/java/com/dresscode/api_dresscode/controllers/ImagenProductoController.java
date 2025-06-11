package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.ImagenProducto;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.services.ImagenProductoService;
import com.dresscode.api_dresscode.services.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/imagenes-producto")
public class ImagenProductoController extends BaseController<ImagenProducto, Long> {

    private final ImagenProductoService imagenProductoService;
    private final ProductoService productoService;

    public ImagenProductoController(ImagenProductoService imagenProductoService, ProductoService productoService) {
        super(imagenProductoService);
        this.imagenProductoService = imagenProductoService;
        this.productoService = productoService;
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ImagenProducto>> getImagenesByProductoId(@PathVariable Long productoId) {
        List<ImagenProducto> imagenes = imagenProductoService.getImagenesByProductoId(productoId);
        return ResponseEntity.ok(imagenes);
    }
    @PostMapping("/upload")
    public ResponseEntity<ImagenProducto> uploadImagen(
            @RequestParam("productoId") Long productoId,
            @RequestParam("image") MultipartFile file) throws IOException {

        // Guardar archivo en disco
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path imagePath = Paths.get("uploads", fileName);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, file.getBytes());

        // Asociar imagen al producto
        Producto producto = productoService.findById(productoId);
        ImagenProducto imagenProducto = new ImagenProducto();
        imagenProducto.setProducto(producto);
        imagenProducto.setUrlImagen("/uploads/" + fileName); // Guarda la URL relativa

        // ASIGNA UN VALOR POR DEFECTO A 'principal'
        imagenProducto.setPrincipal(false); // o true si es la primera imagen

        imagenProductoService.save(imagenProducto);

        return ResponseEntity.ok(imagenProducto);
    }
}