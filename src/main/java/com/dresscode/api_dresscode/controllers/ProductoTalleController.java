package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.ProductoTalleDTO;
import com.dresscode.api_dresscode.entities.ProductoTalle;
import com.dresscode.api_dresscode.services.ProductoTalleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/producto-talles")
public class ProductoTalleController extends BaseController<ProductoTalle, Long> {

    private final ProductoTalleService productoTalleService;

    public ProductoTalleController(ProductoTalleService productoTalleService) {
        super(productoTalleService);
        this.productoTalleService = productoTalleService;
    }

    @PostMapping("/crear/{productoId}/talle/{talleId}")
    public ResponseEntity<ProductoTalle> crearProductoTalle(@PathVariable Long productoId, @PathVariable Long talleId, @RequestBody Map<String, Integer> body) {
        Integer cantidad = body.get("cantidad");
        ProductoTalle nuevo = productoTalleService.crearProductoTalle(productoId, talleId, cantidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }


    @GetMapping("/producto/{productoId}/cantidad-total")
    public ResponseEntity<Integer> obtenerCantidadTotal(@PathVariable Long productoId) {
        Integer total = productoTalleService.obtenerCantidadTotalDeProducto(productoId);
        return ResponseEntity.ok(total);
    }

    @PutMapping("/{productoTalleId}/cantidad")
    public ResponseEntity<ProductoTalle> actualizarCantidad(
            @PathVariable Long productoTalleId,
            @RequestBody Map<String, Integer> body) {

        Integer cantidad = body.get("cantidad");
        ProductoTalle actualizado = productoTalleService.actualizarCantidad(productoTalleId, cantidad);
        return ResponseEntity.ok(actualizado);
    }

}