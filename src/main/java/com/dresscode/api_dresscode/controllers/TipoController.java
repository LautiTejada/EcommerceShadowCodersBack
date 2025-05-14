package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Tipo;
import com.dresscode.api_dresscode.services.TipoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/tipos")
@RequiredArgsConstructor

public class TipoController {

    private TipoService tipoService;

    @GetMapping
    public ResponseEntity<List<Tipo>> obtenerTodosLosTipos() {
        List<Tipo> tipos  = tipoService.getAllTipos();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tipo> obtenerTipoPorId(@PathVariable Long tipoId) {
        Tipo tipo = tipoService.getTipoById(tipoId);
        return ResponseEntity.ok(tipo);
    }

    @PostMapping
    public ResponseEntity<Tipo> crearTipo(@Valid @RequestBody Tipo tipo) {
        Tipo nuevoTipo = tipoService.createTipo(tipo);
        return ResponseEntity.status(201).body(nuevoTipo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tipo> editarTipo(@PathVariable Long tipoId,@Valid @RequestBody Tipo tipo ){
        Tipo tipoEditado = tipoService.updateTipo(tipoId, tipo);
        return ResponseEntity.ok(tipoEditado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Tipo> eliminarTipoPorId(@PathVariable Long tipoId){
        tipoService.deleteTipo(tipoId);
        return  ResponseEntity.noContent().build();
    }

    @GetMapping("/{tipoId}/categorias")
    public ResponseEntity<List<Categoria>> obtenerCategoriasPorTipo(@PathVariable Long tipoId){
        List<Categoria> categorias = tipoService.getCategoriasByTipoId(tipoId);
        return ResponseEntity.ok(categorias);
    }

}
