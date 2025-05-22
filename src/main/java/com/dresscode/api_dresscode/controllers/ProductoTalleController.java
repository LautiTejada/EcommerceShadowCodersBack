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

@RestController
@RequestMapping("/api/producto-talles")
public class ProductoTalleController extends BaseController<ProductoTalle, Long> {

    private final ProductoTalleService productoTalleService;

    public ProductoTalleController(ProductoTalleService productoTalleService) {
        super(productoTalleService);
        this.productoTalleService = productoTalleService;
    }

    @PostMapping
    public ResponseEntity<ProductoTalle> crearProductoTalle(
            @Valid @RequestBody ProductoTalleDTO request
    ) {
        ProductoTalle nuevo = productoTalleService.crearProductoTalle(
                request.getProductoId(),
                request.getTalleId(),
                request.getCantidad()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }


    @GetMapping("/producto/{productoId}/cantidad-total")
    public ResponseEntity<Integer> obtenerCantidadTotal(@PathVariable Long productoId) {
        Integer total = productoTalleService.obtenerCantidadTotalDeProducto(productoId);
        return ResponseEntity.ok(total);
    }

}