package com.int_solutions.sys.dto.userDto;

import com.int_solutions.sys.util.validationI.IdentificacionE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class PersonRequestDto {
    @NotBlank(message = "La cédula de identificación es obligatoria")
    @Pattern(regexp = "^\\d{10}$", message = "La cédula debe contener exactamente 10 dígitos numéricos")
    @IdentificacionE
    private String identificacion;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 3, max = 15, message = "Los nombres deben tener entre 3 y 15 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Los nombres no permiten números ni caracteres especiales")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 3, max = 15, message = "Los apellidos deben tener entre 3 y 15 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Los apellidos no permiten números ni caracteres especiales")
    private String apellidos;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El número de teléfono debe contener exactamente 10 dígitos y no se permiten letras")
    private String telefono;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe contener un @ y un dominio válido")
    private String correo;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 10, message = "La dirección debe tener al menos 10 caracteres")
    private String direccion;
}
