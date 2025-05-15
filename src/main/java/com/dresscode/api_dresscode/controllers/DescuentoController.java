package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Descuento;
import com.dresscode.api_dresscode.entities.DescuentoProducto;
import com.dresscode.api_dresscode.services.DescuentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/descuentos")
@RequiredArgsConstructor

public class DescuentoController {

    private final DescuentoService descuentoService;

    @GetMapping
    public ResponseEntity<List<Descuento>> obtenerTodosLosDescuentos(){
        List<Descuento> descuentos = descuentoService.getAllDescuentos();
        return ResponseEntity.ok(descuentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Descuento> obtenerDescuentoPorId (@PathVariable Long id){
        Descuento descuento = descuentoService.getDescuentoById(id);
        return ResponseEntity.ok(descuento);
    }

    @PostMapping
    public ResponseEntity<Descuento> crearDescuento(@RequestBody Descuento descuento){
        Descuento nuevoDescuento = descuentoService.createDescuento(descuento);
        return ResponseEntity.status(201).body(nuevoDescuento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Descuento> actualizarDescuento(@PathVariable Long id, @RequestBody Descuento descuento){
        Descuento descuentoActualizado = descuentoService.editarDescuento(id, descuento);
        return ResponseEntity.ok(descuentoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDescuento(@PathVariable Long id){
        descuentoService.deleteDescuento(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idDescuento}/productos/{idProducto}")
    public ResponseEntity<Void> agregarProductoADescuento(@PathVariable Long idDescuento, @PathVariable Long idProducto){
       descuentoService.agregarProductoADescuento(idDescuento, idProducto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idDescuento}/productos/{idProducto}")
    public ResponseEntity<Void> eliminarProductoDeDescuento(@PathVariable Long idDescuento, @PathVariable Long idProducto){
        descuentoService.eliminarProductoDeDescuento(idDescuento, idProducto);
        return ResponseEntity.noContent().build();
    }
}
