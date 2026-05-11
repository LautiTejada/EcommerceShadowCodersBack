package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.services.DireccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/direcciones")
public class DireccionController extends BaseController<Direccion, Long> {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        super(direccionService);
        this.direccionService = direccionService;
    }

    @Override
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> save(@jakarta.validation.Valid @RequestBody Direccion entity) throws Exception {
        return super.save(entity);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> update(@PathVariable Long id, @jakarta.validation.Valid @RequestBody Direccion entity) throws Exception {
        return super.update(id, entity);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {
        return super.delete(id);
    }

    @Override
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) throws Exception {
        return super.changeStatus(id);
    }

    @Override
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activate(@PathVariable Long id) throws Exception {
        return super.activate(id);
    }

    @Override
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivate(@PathVariable Long id) throws Exception {
        return super.deactivate(id);
    }
}
