package com.dresscode.api_dresscode.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Categoria extends Base{
    @Column(nullable = false, name = "nombre-categoria")
    private String nombreCategoria;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    private Tipo Tipo;
}
