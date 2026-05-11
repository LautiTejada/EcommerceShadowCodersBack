package com.dresscode.api_dresscode.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "talle_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoTalle extends Base {

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    @JsonIgnore
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_talle", nullable = false)
    private Talle talle;

    @Column(nullable = false)
    @Positive
    private Integer cantidad;
}