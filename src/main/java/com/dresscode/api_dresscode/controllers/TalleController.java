package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Talle;
import com.dresscode.api_dresscode.services.TalleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/talles")

public class TalleController extends BaseController<Talle,Long> {

    private final TalleService talleService;

    public TalleController(TalleService talleService) {
        super(talleService);
        this.talleService = talleService;
    }

    @PostMapping("/{talleId}/productos/{productoId}")
    public ResponseEntity<String> asignarTalleAProducto (@RequestParam  Long talleId, @RequestParam Long productoId){
        talleService.asignarTalleAProducto(productoId, talleId);
        return ResponseEntity.ok("Talle asignado "+ talleId+", al producto: "+ productoId);
    }

    @DeleteMapping("/{talleId}/productos/{productoId}")
    public ResponseEntity<Talle> eliminarTalleDeProducto(@PathVariable Long productoId, @PathVariable Long talleId){
        talleService.eliminarTalleDeProducto(productoId, talleId);
        return ResponseEntity.noContent().build();
    }

}
