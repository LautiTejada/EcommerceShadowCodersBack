package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.ProductoTalleRequest;
import com.dresscode.api_dresscode.entities.ProductoTalle;
import com.dresscode.api_dresscode.services.ProductoTalleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/producto-talles")
@RequiredArgsConstructor
public class ProductoTalleController {

    private final ProductoTalleService productoTalleService;

    @PostMapping
    public ResponseEntity<ProductoTalle> crearProductoTalle(
            @Valid @RequestBody ProductoTalleRequest request
    ) {
        ProductoTalle nuevo = productoTalleService.crearProductoTalle(
                request.getProductoId(),
                request.getTalleId(),
                request.getCantidad()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }


    @PutMapping("/{productoTalleId}")
    public ResponseEntity<ProductoTalle> actualizarCantidad(
            @PathVariable Long productoTalleId,
            @RequestParam Integer nuevaCantidad
    ) {
        ProductoTalle actualizado = productoTalleService.actualizarCantidad(productoTalleId, nuevaCantidad);
        return ResponseEntity.ok(actualizado);
    }


    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ProductoTalle>> obtenerTallesPorProducto(@PathVariable Long productoId) {
        List<ProductoTalle> lista = productoTalleService.obtenerTallesPorProducto(productoId);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/producto/{productoId}/cantidad-total")
    public ResponseEntity<Integer> obtenerCantidadTotal(@PathVariable Long productoId) {
        Integer total = productoTalleService.obtenerCantidadTotalDeProducto(productoId);
        return ResponseEntity.ok(total);
    }

    @DeleteMapping("/{productoTalleId}")
    public ResponseEntity<Void> eliminarProductoTalle(@PathVariable Long productoTalleId) {
        productoTalleService.eliminarProductoTalle(productoTalleId);
        return ResponseEntity.noContent().build();
    }
}