package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.CategoriaDTO;
import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.services.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor

public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> obtenerTodasLasCategorias(){
        List<Categoria> categorias = categoriaService.getAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{categoriaId}")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(@PathVariable Long categoriaId){
        Categoria categoria = categoriaService.getCategoriaById(categoriaId);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping("/tipo/{tipoId}")
    public ResponseEntity<Categoria> crearCategoria(@PathVariable Long tipoId ,@Valid @RequestBody CategoriaDTO categoria){
        Categoria nuevaCategoria = categoriaService.createCategoria(tipoId ,categoria);
        return ResponseEntity.status(201).body(nuevaCategoria);
    }

    @PutMapping("/{categoriaId}")
    public ResponseEntity<Categoria> actualizarCategoria(@PathVariable Long categoriaId, @RequestBody Categoria categoria){
        Categoria categoriaActualizada = categoriaService.updateCategoria(categoriaId, categoria);
        return ResponseEntity.ok(categoriaActualizada);
    }

    @DeleteMapping("/{categoriaId}")
    public  ResponseEntity<Categoria> eliminarCategoria(@PathVariable Long categoriaId){
        categoriaService.deleteCategoria(categoriaId);
        return ResponseEntity.noContent().build();
    }
}
