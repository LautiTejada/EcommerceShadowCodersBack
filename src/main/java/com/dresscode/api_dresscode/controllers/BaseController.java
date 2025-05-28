package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Base;
import com.dresscode.api_dresscode.services.BaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.Serializable;

@RequiredArgsConstructor
public abstract class BaseController<E extends Base, ID extends Serializable> {

    protected final BaseService<E, ID> service;

    @GetMapping
    public ResponseEntity<?> getAll() throws Exception {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/paged")
    public ResponseEntity<?> getAll(Pageable pageable) throws Exception {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActive() throws  Exception {
        return ResponseEntity.ok(service.findAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable ID id) throws Exception {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody E entity) throws Exception {
        E saved = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable ID id, @Valid @RequestBody E entity) throws Exception {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable ID id) throws Exception {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable ID id) throws Exception {
        E updatedEntity = service.changeStatus(id);
        return ResponseEntity.ok(updatedEntity);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable ID id) throws Exception {
        E entity = service.activate(id);
        return ResponseEntity.ok(entity);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable ID id) throws Exception {
        E entity = service.deactivate(id);
        return ResponseEntity.ok(entity);
    }


}

