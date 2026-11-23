package com.int_solutions.sys.entity.diagnosis;

import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.workOrder.Order;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "diagnostico")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diagnostico_id")
    private Long diagnosticoId;

    @Column(name = "causa_probable_tecnico", nullable = false, columnDefinition = "TEXT")
    private String causaProbableTecnico;

    @Column(name = "fecha_emision", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaEmision= LocalDateTime.now();

    @Builder.Default
    @Column(name = "valor_mano_obra", precision = 10, scale = 2)
    private BigDecimal valorManoObra = BigDecimal.ZERO;

    @Column(name = "evidencia_fotografica", length = 255)
    private String evidenciaFotografica;

    @Column(name = "justificacion_invalida", columnDefinition = "TEXT")
    private String justificacionInvalida;

    @Column(name = "pruebas_realizadas", nullable = false, columnDefinition = "TEXT")
    private String pruebasRealizadas;

    @Column(name = "repuestos_requeridos", columnDefinition = "TEXT")
    private String repuestosRequeridos;

    @Column(name = "tiempo_estimado")
    private Integer tiempoEstimado;

    @Column(name = "posibilidades_reparacion", nullable = false, length = 30)
    private String posibilidadesReparacion;

    @Column(name = "observaciones_tecnico", columnDefinition = "TEXT")
    private String observacionesTecnico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Employee tecnico;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_trabajo_id", nullable = false, unique = true)
    private Order ordenTrabajo;

}
