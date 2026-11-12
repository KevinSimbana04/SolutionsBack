package com.int_solutions.sys.entity.equipment;

import com.int_solutions.sys.entity.user.Customer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private Long equipoId;

    @Column(name = "marca", nullable = false, length = 50)
    private String marca;

    @Column(name = "modelo", nullable = false, length = 50)
    private String modelo;

    @Column(name = "numero_de_serie", nullable = false, unique = true, length = 30)
    private String numeroSerie;

    @Column(name = "color", nullable = false, length = 30)
    private String color;

    @Column(name = "accesorios", nullable = false)
    private String accesorios;

    @Column(name = "estado_fisico_inicial", nullable = false)
    private String estadoFisicoInicial;

    @Column(name = "falla_reportada", nullable = false)
    private String fallaReportada;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "fotografia_inicial", nullable = false, updatable = false, columnDefinition = "TEXT")
    private String fotografiaInicial;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Customer cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_equipo_id", nullable = false)
    private EquipmentType tipoEquipo;
}
