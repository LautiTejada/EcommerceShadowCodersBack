package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Marca;
import com.dresscode.api_dresscode.services.MarcaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController extends BaseController<Marca, Long> {

    public MarcaController(MarcaService marcaService) {
        super(marcaService);
    }
}
