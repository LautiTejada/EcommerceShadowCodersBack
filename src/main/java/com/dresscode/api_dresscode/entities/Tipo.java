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
@Table(name = "tipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tipo extends Base{

    @Column(nullable = false, name = "nombre-tipo", unique = true)
    private String nombre;

    @OneToMany(mappedBy = "tipo", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonManagedReference
    private List<Categoria> categorias = new ArrayList<>();
}
