package com.dresscode.api_dresscode.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "calle-producto")  // Nombre según tu UML
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoTalle extends Base {

    @ManyToOne
    @JoinColumn(name = "id-producto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id-talle", nullable = false)
    private Talle talle;

    // Campos adicionales si es necesario (ej: cantidad disponible por talle)
    @Column(nullable = false)
    private Integer cantidad;
}