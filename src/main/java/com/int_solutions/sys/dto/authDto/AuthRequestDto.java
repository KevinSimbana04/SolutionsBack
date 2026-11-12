package com.int_solutions.sys.dto.authDto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {
    @NotBlank(message = "El nombre de usuario o cédula es obligatorio")
    @JsonAlias({"userName", "usuario", "cedula", "identificacion"})
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @JsonAlias({"password", "clave"})
    private String contrasenia;
}
