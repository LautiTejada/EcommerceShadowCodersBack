package com.dresscode.api_dresscode.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Descuento extends Base{
    @Column(nullable = false, name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(nullable = false, name = "fecha_cierre")
    private LocalDate fechaCierre;

    @Column(nullable = false, name = "porcentaje_descuento")
    private Double porcentajeDescuento;

    @OneToMany(mappedBy = "descuento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DescuentoProducto> productos = new ArrayList<>();
}
