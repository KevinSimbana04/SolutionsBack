package com.int_solutions.sys.entity.budget;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_repuesto")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailSparePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detallerepuesto_id")
    private Long id;

    @Column(name = "nombre_repuesto", nullable = false, length = 120)
    private String nombreRepuesto;

    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @DecimalMin(value = "0.00", message = "El costo unitario no puede ser negativo")
    @Column(name = "costo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Builder.Default
    @Column(name = "utilizado_reparacion", nullable = false)
    private Boolean utilizadoReparacion = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "presupuesto_id", nullable = false)
    private Budget presupuesto;
}
