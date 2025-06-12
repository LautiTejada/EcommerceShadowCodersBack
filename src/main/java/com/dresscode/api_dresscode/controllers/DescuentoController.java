package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.DescuentoDTO;
import com.dresscode.api_dresscode.entities.Descuento;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.services.DescuentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/save")
    public ResponseEntity<Descuento> save(@RequestBody DescuentoDTO descuentoDTO) {
        Descuento nuevoDescuento = descuentoService.add(descuentoDTO);
        return ResponseEntity.ok(nuevoDescuento);
    }

    @PutMapping("/{idDescuento}/edit")
    public ResponseEntity<Descuento> editarDescuento(@PathVariable Long idDescuento,@RequestBody DescuentoDTO descuento){
        Descuento descuentoEditado = descuentoService.updateDescuento(idDescuento, descuento);
        return ResponseEntity.status(HttpStatus.OK).body(descuentoEditado);
    }


    @GetMapping("/{descuentoId}/productos")
    public ResponseEntity<List<Producto>> getProductosDeDescuento(@PathVariable Long descuentoId) {
        List<Producto> productos = descuentoService.getProductosDeDescuento(descuentoId);
        return ResponseEntity.ok(productos);
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
