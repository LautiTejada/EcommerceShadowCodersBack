package com.dresscode.api_dresscode.controllers;


import com.dresscode.api_dresscode.entities.DetalleOrden;
import com.dresscode.api_dresscode.services.DetalleOrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/detalles-orden")

public class DetalleOrdenController extends BaseController{

    private final DetalleOrdenService detalleOrdenService;

    public DetalleOrdenController(DetalleOrdenService detalleOrdenService){
        super(detalleOrdenService);
        this.detalleOrdenService = detalleOrdenService;
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

}
