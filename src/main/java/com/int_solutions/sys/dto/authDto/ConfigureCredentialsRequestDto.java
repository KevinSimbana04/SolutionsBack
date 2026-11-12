package com.int_solutions.sys.dto.authDto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigureCredentialsRequestDto {
    @NotBlank(message = "El usuario actual es obligatorio")
    @JsonAlias({"usuario", "userName", "nombreUsuario"})
    private String usuarioActual;

    @NotBlank(message = "La contraseña actual es obligatoria")
    @JsonAlias({"contraseniadActual", "contraseniaActual", "passwordActual", "password"})
    private String contraseniaActual;

    @JsonAlias({"nuevoNombreUsuario", "newUserName"})
    private String nuevoUsuario;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    @JsonAlias({"nuevaPassword", "newPassword"})
    private String nuevaContrasenia;
}
