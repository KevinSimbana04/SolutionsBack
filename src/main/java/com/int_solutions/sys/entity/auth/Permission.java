package com.int_solutions.sys.entity.auth;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="permiso")
public class Permission {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id_permiso")
    private Long permisoId;

    @Enumerated(EnumType.STRING)
    @Column(name="nombre_permiso", nullable = false, unique = true,
            length=80)
    private PermissionE nombrePermiso;

    @ManyToMany (mappedBy = "permisos")
    @Builder.Default
    private Set<Role> roles= new HashSet<>();
}
