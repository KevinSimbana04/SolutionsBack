package com.int_solutions.sys.entity.budget;

import com.int_solutions.sys.entity.diagnosis.Diagnosis;
import com.int_solutions.sys.entity.workOrder.Order;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "presupuesto")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "presupuesto_id")
    private Long id;

    @Builder.Default
    @Column(name = "costo_mano_obra", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoManoObra = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "costo_repuestos", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoRepuestos = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "confirmacion", nullable = false)
    private Boolean confirmacion = false;

    @Builder.Default
    @Column(name = "valor_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorIva = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_pagar", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPagar = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "estado_presupuesto", nullable = false, length = 25)
    private String estadoPresupuesto = "PENDIENTE_APROBACION";

    @Column(name = "motivo_rechazo", length = 200)
    private String motivoRechazo;

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDateTime fechaLimite;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_trabajo_id", nullable = false, unique = true)
    private Order ordenTrabajo;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diagnostico_id", nullable = false, unique = true)
    private Diagnosis diagnostico;

    @Builder.Default
    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailSparePart> detallesRepuesto = new ArrayList<>();
}
