package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Direccion;
import com.dresscode.api_dresscode.services.DireccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

}
