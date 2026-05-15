package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.DescuentoDTO;
import com.dresscode.api_dresscode.entities.Descuento;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.services.DescuentoService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/descuentos")
public class DescuentoController extends BaseController<Descuento, Long> {

    private final DescuentoService descuentoService;

    public DescuentoController(DescuentoService descuentoService) {
        super(descuentoService);
        this.descuentoService = descuentoService;
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Descuento> save(@Valid @RequestBody DescuentoDTO descuentoDTO) {
        Descuento nuevoDescuento = descuentoService.add(descuentoDTO);
        return ResponseEntity.ok(nuevoDescuento);
    }

    @PutMapping("/{idDescuento}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Descuento> editarDescuento(
            @PathVariable Long idDescuento,
            @Valid @RequestBody DescuentoDTO descuento) {
        Descuento descuentoEditado = descuentoService.updateDescuento(idDescuento, descuento);
        return ResponseEntity.status(HttpStatus.OK).body(descuentoEditado);
    }

    @GetMapping("/{descuentoId}/productos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Producto>> getProductosDeDescuento(@PathVariable Long descuentoId) {
        List<Producto> productos = descuentoService.getProductosDeDescuento(descuentoId);
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/{descuentoId}/productos/{idProducto}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> agregarProductoADescuento(
            @PathVariable Long descuentoId,
            @PathVariable Long idProducto) {
        try {
            Descuento descuento = descuentoService.agregarProductoADescuento(descuentoId, idProducto);
            return ResponseEntity.ok(descuento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{descuentoId}/productos/{idProducto}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarProductoDeDescuento(
            @PathVariable Long descuentoId,
            @PathVariable Long idProducto) {
        descuentoService.eliminarProductoDeDescuento(descuentoId, idProducto);
        return ResponseEntity.noContent().build();
    }
}
