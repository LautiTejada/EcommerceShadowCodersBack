package com.dresscode.api_dresscode.controllers;

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

@Controller
@RequestMapping("/api/ordenes-de-compra")
@RequiredArgsConstructor

public class OrdenDeCompraController {

    private OrdenDeCompraService ordenDeCompraService;

    @GetMapping
    public ResponseEntity<List<OrdenDeCompra>> obtenerTodasLasOrdenes(){
        List<OrdenDeCompra> ordenesDeCompra = ordenDeCompraService.getAllOrdenesDeCompra();
        return ResponseEntity.ok(ordenesDeCompra);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenDeCompra> obtenerOrdenPorId(@PathVariable Long ordenId){
        OrdenDeCompra orden = ordenDeCompraService.getOrdenDeCompraById(ordenId);
        return ResponseEntity.ok(orden);
    }

    @PostMapping
    public ResponseEntity<OrdenDeCompra> crearOrdenDeCompra(@Valid @RequestBody OrdenDeCompra orden){
        OrdenDeCompra nuevaOrden = ordenDeCompraService.createOrdenDeCompra(orden);
        return ResponseEntity.status(201).body(nuevaOrden);
    }

    @PutMapping("/{id}/actualizar-estado")
    public ResponseEntity<OrdenDeCompra> actualizarEstadoOrden(@PathVariable Long ordenId, @RequestParam EstadoOrden estadoOrden){
        OrdenDeCompra ordenActualizada = ordenDeCompraService.actualizarEstadoOrden(ordenId, estadoOrden);
        return ResponseEntity.ok(ordenActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrdenDeCompra> eliminarOrdenDeCompra(@PathVariable Long ordenId){
        ordenDeCompraService.eliminarOrden(ordenId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<OrdenDeCompra>> traerOrdenesPorUsuario(@PathVariable Long idUsuario){
        List<OrdenDeCompra> ordenes = ordenDeCompraService.getOrdenesByUsuario(idUsuario);
        return ResponseEntity.ok(ordenes);
    }
}
