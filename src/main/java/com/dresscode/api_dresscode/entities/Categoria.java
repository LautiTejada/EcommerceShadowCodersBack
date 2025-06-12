package com.dresscode.api_dresscode.entities;

import com.fasterxml.jackson.annotation.*;
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
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Categoria extends Base{
    @Column(nullable = false, name = "nombre-categoria", unique = true, length = 50)
    private String nombreCategoria;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)

    private Tipo tipo;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<Producto> productos = new ArrayList<>();

}