package com.dresscode.api_dresscode.controllers;


import com.dresscode.api_dresscode.entities.Categoria;
import com.dresscode.api_dresscode.entities.Tipo;
import com.dresscode.api_dresscode.services.TipoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos")

public class TipoController extends BaseController<Tipo, Long> {

    private final TipoService tipoService;

    public TipoController(TipoService tipoService){
        super(tipoService);
        this.tipoService = tipoService;
    }


    @DeleteMapping("/{tipoId}")
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
