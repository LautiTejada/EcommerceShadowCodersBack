package com.dresscode.api_dresscode.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_usuario", "id_producto"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Favorito extends Base {

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "fecha_agregado", nullable = false)
    private LocalDateTime fechaAgregado;

    @PrePersist
    protected void onCreate() {
        if (this.fechaAgregado == null) {
            this.fechaAgregado = LocalDateTime.now();
        }
    }
}
