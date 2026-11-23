package com.int_solutions.sys.entity.repair;

import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.workOrder.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reparacion")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Repair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reparacion_id")
    private Long id;

    @Column(name = "fecha_inicio", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaInicio= LocalDateTime.now();

    @Column(name = "estado_operativo", nullable = false, length = 30)
    private String estadoOperativo;

    @Column(name = "resultado_prueba", length = 30)
    private String resultadoPrueba;

    @Column(name = "motivo_fallo", columnDefinition = "TEXT")
    private String motivoFallo;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "evidencia")
    private String evidencia;

    @Column(name = "bitacora_de_acciones", nullable = false, columnDefinition = "TEXT")
    private String bitacoraDeAcciones;

    @Column(name = "historial_pruebas", columnDefinition = "TEXT")
    private String historialPruebas;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Employee tecnico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_trabajo_id", nullable = false)
    private Order ordenTrabajo;
}
