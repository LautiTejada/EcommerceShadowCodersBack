package com.dresscode.api_dresscode.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "descuento")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Descuento extends Base{
    @Column(nullable = false, name = "fecha-inicio")
    private LocalDate fechaInicio;  // Usar LocalDate para fechas

    @Column(nullable = false, name = "fecha-cierre")
    private LocalDate fechaCierre;

    @Column(nullable = false, name = "porcentaje-descuento")
    private Integer porcentajeDescuento;

    @OneToMany(mappedBy = "descuento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DescuentoProducto> productos = new ArrayList<>();
}
