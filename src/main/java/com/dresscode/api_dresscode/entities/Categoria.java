package com.dresscode.api_dresscode.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Categoria extends Base{
    @Column(nullable = false, name = "nombre-categoria", unique = true, length = 50)
    private String nombreCategoria;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    @JsonBackReference
    private Tipo tipo;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<Producto> productos = new ArrayList<>();

}