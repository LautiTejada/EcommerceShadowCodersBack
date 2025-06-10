package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.ImagenProductoDTO;
import com.dresscode.api_dresscode.dtos.ProductoDTO;
import com.dresscode.api_dresscode.entities.ImagenProducto;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.enums.Marca;
import com.dresscode.api_dresscode.services.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController extends BaseController<Producto, Long> {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        super(productoService);
        this.productoService = productoService;
    }


    @GetMapping("/filtrar")
    public ResponseEntity<List<Producto>> filtrarProductos(
            @RequestParam(required = false) List<Long> tipoIds,
            @RequestParam(required = false) List<Long> categoriaIds,
            @RequestParam(required = false) List<Marca> marcas,
            @RequestParam(required = false) Integer precioMin,
            @RequestParam(required = false) Integer precioMax
    ) {
        List<Producto> productos = productoService.filtrarProductos(tipoIds, categoriaIds, marcas, precioMin, precioMax);
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/{categoriaId}")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoDTO producto, @PathVariable Long categoriaId){
        Producto nuevoProducto = productoService.createProducto(producto, categoriaId);
        return ResponseEntity.status(201).body(nuevoProducto);
    }

    @PutMapping("/{productoId}")
    public ResponseEntity<Producto> editarProducto(@PathVariable Long productoId, @Valid @RequestBody Producto producto){
        Producto nuevoProducto = productoService.updateProducto(productoId, producto);
        return ResponseEntity.ok(nuevoProducto);
    }


    @PutMapping("/{productoId}/cambiar-etado")
    public ResponseEntity<Producto> cambiarEstadoProducto(@PathVariable Long productoId, @RequestParam Boolean nuevoEstado){
        Producto productoActualizado = productoService.cambiarEstadoProducto(productoId, nuevoEstado);
        return ResponseEntity.ok(productoActualizado);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Producto>> traerProductosPorCategoria (@PathVariable Long categoriaId){
        List<Producto> productos = productoService.getProductosByCategoria(categoriaId);
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/imagen/{productoId}")
    public ResponseEntity<ImagenProducto> createImagen(@PathVariable Long productoId, @RequestBody ImagenProductoDTO imagenProducto) {
        ImagenProducto imagen = ImagenProducto.builder()
                .urlImagen(imagenProducto.getUrlImagen())
                .principal(imagenProducto.getPrincipal())
                .build();
        ImagenProducto nuevaImagen = productoService.agregarImagenAProducto(productoId, imagen);
        return ResponseEntity.ok(nuevaImagen);
    }

    @PutMapping("/imagen/{imagenId}")
    public ResponseEntity<ImagenProducto> editarImagen(@PathVariable Long imagenId, @RequestBody ImagenProductoDTO imagenProductoDTO) {
        ImagenProducto imagenActualizada = productoService.editarImagenProducto(imagenId, imagenProductoDTO);
        return ResponseEntity.ok(imagenActualizada);
    }

    @DeleteMapping("/imagen/{imagenId}")
    public ResponseEntity<Producto> eliminarImagen(@PathVariable Long imagenId) {
        Producto producto = productoService.eliminarImagenProducto(imagenId);
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/imagen/{productoId}")
    public ResponseEntity<List<ImagenProducto>> getImagenes(@PathVariable Long productoId) {
        List<ImagenProducto> imagenes = productoService.getImagenesByProducto(productoId);
        return ResponseEntity.ok(imagenes);
    }

}
