package com.int_solutions.sys.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Person {

    @Column(name = "nombres", nullable = false, length = 50)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 50)
    private String apellidos;

    @Column(name = "identificacion", nullable = false, unique = true, length = 10)
    private String identificacion;

    @Column(name = "telefono", nullable = false, length = 10)
    private String telefono;

    @Column(name = "direccion", nullable = false)
    private String direccion;
}
