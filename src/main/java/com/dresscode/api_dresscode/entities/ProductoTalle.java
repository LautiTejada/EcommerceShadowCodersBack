package com.dresscode.api_dresscode.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "talle-producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoTalle extends Base {

    @ManyToOne
    @JoinColumn(name = "id-producto", nullable = false)
    @JsonBackReference
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id-talle", nullable = false)
    private Talle talle;

    @Column(nullable = false)
    @Positive
    @Max(100)
    @Builder.Default
    private Integer cantidad = 0;
}