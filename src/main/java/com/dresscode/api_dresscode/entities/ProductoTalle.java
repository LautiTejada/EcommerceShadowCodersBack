package com.dresscode.api_dresscode.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "calle-producto")  // Nombre según tu UML
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoTalle extends Base {

    @ManyToOne
    @JoinColumn(name = "id-producto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id-talle", nullable = false)
    private Talle talle;
}