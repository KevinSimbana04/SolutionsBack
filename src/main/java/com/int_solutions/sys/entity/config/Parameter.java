package com.int_solutions.sys.entity.config;

import com.int_solutions.sys.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name="parametro")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private Long parametroId;

    @Column(name = "clave", nullable = false, unique = true, length = 60)
    private String clave;

    @Column(name = "valor", nullable = false, length = 50)
    private String valor;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_actualizacion")
    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private User usuario;
}
