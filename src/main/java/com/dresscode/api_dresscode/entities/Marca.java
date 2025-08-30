package com.dresscode.api_dresscode.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "colores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marca extends Base{

    @Column(unique = true, nullable = false)
    private String nombreMarca;
}
