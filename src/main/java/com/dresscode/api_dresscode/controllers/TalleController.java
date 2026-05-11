package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Talle;
import com.dresscode.api_dresscode.services.TalleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> asignarTalleAProducto (@PathVariable Long talleId, @PathVariable Long productoId){
        talleService.asignarTalleAProducto(productoId, talleId);
        return ResponseEntity.ok("Talle asignado "+ talleId+", al producto: "+ productoId);
    }

    @DeleteMapping("/{talleId}/productos/{productoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Talle> eliminarTalleDeProducto(@PathVariable Long productoId, @PathVariable Long talleId){
        talleService.eliminarTalleDeProducto(productoId, talleId);
        return ResponseEntity.noContent().build();
    }

    /** PATCH alias — frontend uses PATCH /talles/{id}/status */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeStatusPatch(@PathVariable Long id) throws Exception {
        return super.changeStatus(id);
    }

}
