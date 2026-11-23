package com.int_solutions.sys.entity.workOrder;

import com.int_solutions.sys.entity.equipment.Equipment;
import com.int_solutions.sys.entity.user.Customer;
import com.int_solutions.sys.entity.user.Employee;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "ordenTrabajo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Long ordenId;

    @Column(name = "codigo", nullable = false,unique = true, length = 50)
    private String codigo;

    @Builder.Default
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    @Column(name = "fecha_estimada_entreega")
    private LocalDate fechaEstimadaEntrega;

    @Column(name = "fecha_entrega" )
    private LocalDate fechaEntrega;

    @Column(name = "persona_recibe", length = 120)
    private String personaRecibe;

    @Column(name = "motivo", columnDefinition ="TEXT")
    private String motivo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_orden_id", nullable = false)
    private OrderStatus estadoOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrativo_id", nullable = false)
    private Employee administrativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Employee tecnico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipment equipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Customer cliente;
}
