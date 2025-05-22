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


    @PutMapping("/{categoriaId}")
    public ResponseEntity<Categoria> editarCategoria(@PathVariable Long categoriaId, @RequestBody Categoria categoria){
        Categoria categoriaActualizada = categoriaService.update(categoriaId, categoria);
        return ResponseEntity.status(HttpStatus.OK).body(categoriaActualizada);
    }

    @DeleteMapping("/{categoriaId}")
    public ResponseEntity<Categoria> eliminarCategoria(@PathVariable Long categoriaId){
        categoriaService.delete(categoriaId);
        return ResponseEntity.noContent().build();
    }


}
