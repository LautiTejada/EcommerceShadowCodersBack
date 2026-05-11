package com.dresscode.api_dresscode.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "estadisticas_auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasAudit extends Base {

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(nullable = false, length = 200)
    private String endpoint;

    @Column(nullable = false)
    private LocalDateTime fechaAcceso;

    @Column(columnDefinition = "text")
    private String detalles;
}
