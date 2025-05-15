package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.services.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/productos")
@RequiredArgsConstructor

public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos(){
        List<Producto> productos = productoService.getAllProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long productoId){
        Producto producto = productoService.getProductoById(productoId);
        return ResponseEntity.ok(producto);

    }

    @PostMapping("/{categoriaId}")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto producto, @PathVariable Long categoriaId){
        Producto nuevoProducto = productoService.createProducto(producto, categoriaId);
        return ResponseEntity.status(201).body(nuevoProducto);
    }

    @PutMapping("/{productoId}")
    public ResponseEntity<Producto> editarProducto(@PathVariable Long productoId, @Valid @RequestBody Producto producto){
        Producto nuevoProducto = productoService.updateProducto(productoId, producto);
        return ResponseEntity.ok(nuevoProducto);
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<Producto> eliminarProducto(@PathVariable Long productoId){
        productoService.deleteProducto(productoId);
        return ResponseEntity.noContent().build();
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
}
