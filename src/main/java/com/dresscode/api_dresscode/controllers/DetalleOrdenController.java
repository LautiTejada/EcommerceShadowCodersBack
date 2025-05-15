package com.dresscode.api_dresscode.controllers;


import com.dresscode.api_dresscode.entities.DetalleOrden;
import com.dresscode.api_dresscode.services.DetalleOrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/detalles-orden")
@RequiredArgsConstructor

public class DetalleOrdenController {
    private final DetalleOrdenService detalleOrdenService;
    @GetMapping
    public ResponseEntity<List<DetalleOrden>> obtenerTodosLosDetalles(){
        List<DetalleOrden> detalles = detalleOrdenService.getAllDetalles();
        return ResponseEntity.ok(detalles);

    }

    @GetMapping("/{detalleId}")
    public ResponseEntity<DetalleOrden> obtenerDetallesPorId(@PathVariable Long detalleId){
        DetalleOrden detalle = detalleOrdenService.getDetalleById(detalleId);
        return ResponseEntity.ok(detalle);
    }

    @PostMapping("/orden/{ordenId}/producto/{productoId}")
    public ResponseEntity<DetalleOrden> agregarDetalleAOrden(@PathVariable Long ordenId, @PathVariable Long productoId, @RequestBody DetalleOrden detalleOrden){
        DetalleOrden nuevoDetalle = detalleOrdenService.agregarDetalleAOrden(ordenId,productoId, detalleOrden);
        return ResponseEntity.status(201).body(nuevoDetalle);

    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<DetalleOrden>> obtenerDetallesPorOrden(@PathVariable Long ordenId){
        List<DetalleOrden> detalles = detalleOrdenService.obtenerDetallesPorOrden(ordenId);
        return ResponseEntity.ok(detalles);

    }

    @PutMapping("/{detalleId}")
    public ResponseEntity<DetalleOrden> actualizarDetallerOrden(@PathVariable Long detalleId, @RequestParam Integer nuevaCantidad){
        DetalleOrden detalleActualizado = detalleOrdenService.actualizarDetalleOrden(detalleId,nuevaCantidad);
        return ResponseEntity.status(201).body(detalleActualizado);
    }

    @DeleteMapping("/{detalleId}")
    public  ResponseEntity<Void> eliminarDetalleDeOrden(@PathVariable Long detalleId){
        detalleOrdenService.eliminarDetalleDeOrden(detalleId);
        return ResponseEntity.noContent().build();
    }
}
