package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.ImagenProductoDTO;
import com.dresscode.api_dresscode.dtos.ProductoDTO;
import com.dresscode.api_dresscode.entities.ImagenProducto;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.services.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /** Devuelve solo productos activos (uso público / frontend). */
    @Override
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(productoService.getProductosActivos());
    }

    /** Devuelve solo productos activos (alias heredado — mismo comportamiento). */
    @Override
    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() {
        return ResponseEntity.ok(productoService.getProductosActivos());
    }

    /** Devuelve TODOS los productos (activos + inactivos) — solo para el panel admin. */
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Producto>> getTodosLosProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @PostMapping("/{categoriaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoDTO producto, @PathVariable Long categoriaId){
        Producto nuevoProducto = productoService.createProducto(producto, categoriaId);
        return ResponseEntity.status(201).body(nuevoProducto);
    }


//    @PutMapping("/{productoId}/editar")
//    public ResponseEntity<Producto> editarProducto(@PathVariable Long productoId, @Valid @RequestBody ProductoDTO producto) {
//        Producto nuevoProducto = productoService.updateProducto(productoId, producto);
//        return ResponseEntity.ok(nuevoProducto);
//    }


    @PutMapping("/{productoId}/cambiar-etado")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImagenProducto> createImagen(@PathVariable Long productoId, @Valid @RequestBody ImagenProductoDTO imagenProducto) {
        ImagenProducto imagen = ImagenProducto.builder()
                .urlImagen(imagenProducto.getUrlImagen())
                // .principal(imagenProducto.getPrincipal()) // Si el método principal no existe, comentar o implementar
                .build();
        ImagenProducto nuevaImagen = productoService.agregarImagenAProducto(productoId, imagen);
        return ResponseEntity.ok(nuevaImagen);
    }

    @PutMapping("/imagen/{imagenId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImagenProducto> editarImagen(@PathVariable Long imagenId, @Valid @RequestBody ImagenProductoDTO imagenProductoDTO) {
        ImagenProducto imagenActualizada = productoService.editarImagenProducto(imagenId, imagenProductoDTO);
        return ResponseEntity.ok(imagenActualizada);
    }

    @DeleteMapping("/imagen/{imagenId}")
    @PreAuthorize("hasRole('ADMIN')")
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
