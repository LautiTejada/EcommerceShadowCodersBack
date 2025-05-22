package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.OrdenDeCompraDTO;
import com.dresscode.api_dresscode.entities.OrdenDeCompra;
import com.dresscode.api_dresscode.entities.enums.EstadoOrden;
import com.dresscode.api_dresscode.services.OrdenDeCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-de-compra")

public class OrdenDeCompraController extends BaseController<OrdenDeCompra, Long> {

    private final OrdenDeCompraService ordenDeCompraService;

    public OrdenDeCompraController(OrdenDeCompraService ordenDeCompraService) {
        super(ordenDeCompraService);
        this.ordenDeCompraService = ordenDeCompraService;
    }

    @PostMapping
    public ResponseEntity<OrdenDeCompra> crearOrdenDeCompra(@Valid @RequestBody OrdenDeCompra orden){
        OrdenDeCompra nuevaOrden = ordenDeCompraService.createOrdenDeCompra(orden);
        return ResponseEntity.status(201).body(nuevaOrden);
    }

    @PutMapping("/{ordenId}/actualizar-estado")
    public ResponseEntity<OrdenDeCompra> actualizarEstadoOrden(@PathVariable Long ordenId, @RequestParam EstadoOrden estadoOrden){
        OrdenDeCompra ordenActualizada = ordenDeCompraService.actualizarEstadoOrden(ordenId, estadoOrden);
        return ResponseEntity.ok(ordenActualizada);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<OrdenDeCompra>> traerOrdenesPorUsuario(@PathVariable Long idUsuario){
        List<OrdenDeCompra> ordenes = ordenDeCompraService.getOrdenesByUsuario(idUsuario);
        return ResponseEntity.ok(ordenes);
    }

    @PostMapping("/detalle")
    public ResponseEntity<OrdenDeCompra> crearOrdenConDetalles(@RequestBody OrdenDeCompraDTO ordenCompra) {
        OrdenDeCompra nuevaOrden = ordenDeCompraService.crearOrdenConDetalles(ordenCompra);
        return ResponseEntity.status(201).body(nuevaOrden);
    }

}
