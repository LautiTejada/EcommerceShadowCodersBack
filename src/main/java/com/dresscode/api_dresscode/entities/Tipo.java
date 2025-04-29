package com.dresscode.api_dresscode.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Tipo extends Base{

    @Column(nullable = false, name = "nombre-tipo", unique = true)
    private String nombre;
}
