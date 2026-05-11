package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Color;
import com.dresscode.api_dresscode.services.ColorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colores")
public class ColorController extends BaseController<Color, Long> {

    public ColorController(ColorService colorService) {
        super(colorService);
    }

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> save(@jakarta.validation.Valid @RequestBody Color entity) throws Exception {
        return super.save(entity);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @jakarta.validation.Valid @RequestBody Color entity) throws Exception {
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
