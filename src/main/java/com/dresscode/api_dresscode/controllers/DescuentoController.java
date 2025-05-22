package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.DescuentoDTO;
import com.dresscode.api_dresscode.entities.Descuento;
import com.dresscode.api_dresscode.services.DescuentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/descuentos")

public class DescuentoController extends BaseController<Descuento, Long> {

    private final DescuentoService descuentoService;

    public DescuentoController(DescuentoService descuentoService){
        super(descuentoService);
        this.descuentoService = descuentoService;
    }



    @PutMapping("/{descuentoId}")
    public ResponseEntity<Descuento> actualizarDescuento(@PathVariable Long descuentoId, @RequestBody Descuento descuento){
        Descuento descuentoActualizado = descuentoService.update(descuentoId, descuento);
        return ResponseEntity.ok(descuentoActualizado);
    }


    @PostMapping("/{descuentoId}/productos/{idProducto}")
    public ResponseEntity<Descuento> agregarProductoADescuento(@PathVariable Long descuentoId, @PathVariable Long idProducto){
       descuentoService.agregarProductoADescuento(descuentoId, idProducto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{descuentoId}/productos/{idProducto}")
    public ResponseEntity<Void> eliminarProductoDeDescuento(@PathVariable Long descuentoId, @PathVariable Long idProducto){
        descuentoService.eliminarProductoDeDescuento(descuentoId, idProducto);
        return ResponseEntity.noContent().build();
    }
}
