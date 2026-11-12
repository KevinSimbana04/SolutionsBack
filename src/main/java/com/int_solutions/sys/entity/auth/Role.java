package com.int_solutions.sys.entity.auth;

import com.int_solutions.sys.entity.user.User;
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
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long rolId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_rol", nullable = false, unique = true, length = 30)
    private RoleE nombreRol;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "rol_permisos",
            joinColumns = @JoinColumn(name = "rol_id"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    @Builder.Default
    private Set<Permission> permisos = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<User> usuarios = new HashSet<>();
}
