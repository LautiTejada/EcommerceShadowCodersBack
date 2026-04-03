package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.entities.Color;
import com.dresscode.api_dresscode.services.ColorService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/colores")
public class ColorController extends BaseController<Color, Long> {

    public ColorController(ColorService colorService) {
        super(colorService);
    }
}
