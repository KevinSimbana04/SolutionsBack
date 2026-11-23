package com.int_solutions.sys.entity.workOrder;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "estados_ordenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_orden")
    private Long estadoOrdenId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_estado")
    private OrderStatusE tipoEstado;

}
