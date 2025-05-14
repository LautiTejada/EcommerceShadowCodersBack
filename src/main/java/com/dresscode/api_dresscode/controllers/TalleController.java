package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Talle;
import com.dresscode.api_dresscode.services.TalleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/talles")
@RequiredArgsConstructor

public class TalleController {

    private TalleService talleService;

    @GetMapping
    public ResponseEntity<List<Talle>> obtenerTodosLosTalles() {
        List<Talle> talles = talleService.getAllTalles();
        return ResponseEntity.ok(talles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Talle> obtenerTallePorId(@PathVariable Long talleId) {
        Talle talle = talleService.getTalleById(talleId);
        return  ResponseEntity.ok(talle);
    }

    @PostMapping
    public ResponseEntity<Talle> crearTalle(@Valid @RequestBody Talle talle){
        Talle nuevoTalle = talleService.createTalle(talle);
        return ResponseEntity.status(201).body(nuevoTalle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Talle> editarTalle(@PathVariable Long talleId, @Valid @RequestBody Talle talle){
        Talle talleEditado = talleService.updateTalle(talleId, talle);
        return ResponseEntity.ok(talleEditado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Talle> eliminarTalle(@PathVariable Long talleId){
        talleService.deleteTalle(talleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{talleId}/producto/{productoId}")
    public ResponseEntity<String> asignarTalleAProducto (@RequestParam  Long talleId, @RequestParam Long productoId){
        talleService.asignarTalleAProducto(productoId, talleId);
        return ResponseEntity.ok("Talle asignado "+ talleId+", al producto: "+ productoId);
    }

    @DeleteMapping("/{talleId}/producto/{productoId}")
    public ResponseEntity<Talle> eliminarTalleDeProducto(@PathVariable Long productoId, @PathVariable Long talleId){
        talleService.eliminarTalleDeProducto(productoId, talleId);
        return ResponseEntity.noContent().build();
    }

}
