package com.dresscode.api_dresscode.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "talle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Talle extends Base{
    @Column(name = "tipo-talle", nullable = false)
    private String tipoTalle;

    @OneToMany(mappedBy = "talle", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<ProductoTalle> productos = new ArrayList<>();
}
