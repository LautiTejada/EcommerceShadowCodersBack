package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.CategoriaDTO;
import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.services.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController extends BaseController<Categoria, Long> {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        super(categoriaService);
        this.categoriaService = categoriaService;
    }

    @PostMapping("/{tipoId}")
    public ResponseEntity<Categoria> crearCategoria(@Valid @RequestBody CategoriaDTO categoria, @PathVariable Long tipoId){
        Categoria nuevaCategoria = categoriaService.createCategoria(categoria, tipoId);
        return ResponseEntity.status(201).body(nuevaCategoria);
    }


    @PutMapping("/{categoriaId}/edit/{tipoId}")
    public ResponseEntity<Categoria> editarCategoria(@PathVariable Long categoriaId, @RequestBody CategoriaDTO categoria, @PathVariable Long tipoId){
        Categoria categoriaActualizada = categoriaService.updateCategoria(categoriaId, categoria, tipoId);
        return ResponseEntity.status(HttpStatus.OK).body(categoriaActualizada);
    }

    @DeleteMapping("/{categoriaId}")
    public ResponseEntity<Categoria> eliminarCategoria(@PathVariable Long categoriaId){
        categoriaService.delete(categoriaId);
        return ResponseEntity.noContent().build();
    }


}
