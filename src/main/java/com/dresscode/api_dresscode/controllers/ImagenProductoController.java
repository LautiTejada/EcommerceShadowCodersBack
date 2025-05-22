package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.ImagenProducto;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.services.ImagenProductoService;
import com.dresscode.api_dresscode.services.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imagenes-producto")

public class ImagenProductoController extends BaseController<ImagenProducto, Long>{

    private final ImagenProductoService imagenProductoService;

    public ImagenProductoController(ImagenProductoService imagenProductoService){
        super(imagenProductoService);
        this.imagenProductoService = imagenProductoService;
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ImagenProducto>> getImagenesByProductoId(@PathVariable Long productoId) {
        List<ImagenProducto> imagenes = imagenProductoService.getImagenesByProductoId(productoId);
        return ResponseEntity.ok(imagenes);
    }

}
