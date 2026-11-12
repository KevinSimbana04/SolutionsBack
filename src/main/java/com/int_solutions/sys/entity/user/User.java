package com.int_solutions.sys.entity.user;

import com.int_solutions.sys.entity.auth.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long usuarioId;

    @Column(name = "correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "nombre_usuario", unique = true, nullable = false)
    private String nombreUsuario;

    @Column(name = "contrasenia", nullable = false)
    private String contrasenia;

    @Column(name = "foto_usuario")
    private String fotoUsuario;

    @Column(name = "fecha_bloqueo")
    private LocalDateTime fechaBloqueo;

    @Column(name = "bloqueado")
    @Builder.Default
    private Boolean bloqueado = false;

    @Column(name = "estado_cuenta")
    @Builder.Default
    private Boolean estadoCuenta = false;

    @Column(name = "intentos_fallidos")
    @Builder.Default
    private int intentosFallidos = 0;

    @Column(name = "fecha_creacion")
    @Builder.Default
    private LocalDate fechaCreacion = LocalDate.now();

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "requiere_cambio_clave")
    @Builder.Default
    private Boolean requiereCambioClave = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Employee empleado;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Customer cliente;
}
