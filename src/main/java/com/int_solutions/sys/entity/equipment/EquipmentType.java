package com.int_solutions.sys.entity.equipment;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_equipo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_equipo")
    private Long tipoEquipoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre_equipo", nullable = false, unique = true, length = 30)
    private TypeE nombreEquipo;
}
