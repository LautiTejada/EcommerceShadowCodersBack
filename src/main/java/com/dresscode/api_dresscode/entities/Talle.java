package com.dresscode.api_dresscode.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "talle")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Talle extends Base{
    @Column(name = "tipo-talle", nullable = false)
    private String tipoTalle;  // Ej: "S", "M", "L", "XL"

    @OneToMany(mappedBy = "talle", cascade = CascadeType.ALL)
    private List<ProductoTalle> productos = new ArrayList<>();
}
