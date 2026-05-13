package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.OrdenDeCompraDTO;
import com.dresscode.api_dresscode.entities.OrdenDeCompra;
import com.dresscode.api_dresscode.entities.enums.EstadoOrden;
import com.dresscode.api_dresscode.services.OrdenDeCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenDeCompraController extends BaseController<OrdenDeCompra, Long> {

    private final OrdenDeCompraService ordenDeCompraService;

    public OrdenDeCompraController(OrdenDeCompraService ordenDeCompraService) {
        super(ordenDeCompraService);
        this.ordenDeCompraService = ordenDeCompraService;
    }

    /** Sobrescribe el getAll del base para restringirlo a ADMIN. */
    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAll() throws Exception {
        return super.getAll();
    }

    @PutMapping("/{ordenId}/actualizar-estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrdenDeCompra> actualizarEstadoOrden(
            @PathVariable Long ordenId,
            @RequestParam EstadoOrden estadoOrden) {
        OrdenDeCompra ordenActualizada = ordenDeCompraService.actualizarEstadoOrden(ordenId, estadoOrden);
        return ResponseEntity.ok(ordenActualizada);
    }

    @PatchMapping("/{ordenId}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrdenDeCompra> cambiarEstadoOrden(
            @PathVariable Long ordenId,
            @RequestParam EstadoOrden estado) {
        OrdenDeCompra ordenActualizada = ordenDeCompraService.actualizarEstadoOrden(ordenId, estado);
        return ResponseEntity.ok(ordenActualizada);
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN') or @usuarioService.esElMismoUsuario(#idUsuario, authentication.principal.username)")
    public ResponseEntity<List<OrdenDeCompra>> traerOrdenesPorUsuario(@PathVariable Long idUsuario) {
        List<OrdenDeCompra> ordenes = ordenDeCompraService.getOrdenesByUsuario(idUsuario);
        return ResponseEntity.ok(ordenes);
    }

    @PostMapping("/crear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrdenDeCompra> crearOrden(@Valid @RequestBody OrdenDeCompraDTO ordenCompra) {
        OrdenDeCompra nuevaOrden = ordenDeCompraService.crearOrdenConDetalles(ordenCompra);
        return ResponseEntity.status(201).body(nuevaOrden);
    }

    @PostMapping("/detalle")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrdenDeCompra> crearOrdenConDetalles(@Valid @RequestBody OrdenDeCompraDTO ordenCompra) {
        OrdenDeCompra nuevaOrden = ordenDeCompraService.crearOrdenConDetalles(ordenCompra);
        return ResponseEntity.status(201).body(nuevaOrden);
    }
}
