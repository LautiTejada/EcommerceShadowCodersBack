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

    @GetMapping("{/{id}")
    public ResponseEntity<DetalleOrden> obtenerDetallesPorId(@PathVariable Long id){
        DetalleOrden detalle = detalleOrdenService.getDetalleById(id);
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

    @PutMapping("/{id}")
    public ResponseEntity<DetalleOrden> actualizarDetallerOrden(@PathVariable Long id, @RequestParam Integer nuevaCantidad){
        DetalleOrden detalleActualizado = detalleOrdenService.actualizarDetalleOrden(id,nuevaCantidad);
        return ResponseEntity.status(201).body(detalleActualizado);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<Void> eliminarDetalleDeOrden(@PathVariable Long id){
        detalleOrdenService.eliminarDetalleDeOrden(id);
        return ResponseEntity.noContent().build();
    }
}
