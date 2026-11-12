package com.int_solutions.sys.dto.equipmentDto;

import com.int_solutions.sys.entity.equipment.TypeE;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentRequestDto {
    @NotNull(message = "El cliente propietario es obligatorio")
    private Long clienteId;

    @NotNull(message = "El tipo de equipo es obligatorio")
    private TypeE tipoEquipo;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    @NotBlank(message = "El número de serie es obligatorio")
    @Size(min = 5, max = 30, message = "El número de serie debe tener entre 5 y 30 caracteres")
    private String numeroSerie;

    @NotBlank(message = "El color es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El color solo debe contener letras")
    private String color;

    @NotBlank(message = "El campo accesorios es obligatorio")
    private String accesorios;

    @NotBlank(message = "El estado físico inicial es obligatorio")
    @Size(min = 10, message = "El estado físico inicial debe tener al menos 10 caracteres")
    private String estadoFisicoInicial;

    @NotBlank(message = "Es obligatorio adjuntar al menos una fotografía del estado físico inicial del equipo antes de continuar")
    @Pattern(regexp = ".*\\.(jpg|jpeg|png|JPG|JPEG|PNG)$|^data:image/(jpeg|png);base64,.*",
            message = "La fotografía debe estar en formato JPG o PNG")
    private String fotografiaInicial;

    @NotBlank(message = "La falla reportada es obligatoria")
    @Size(min = 10, message = "La falla reportada debe tener al menos 10 caracteres")
    private String fallaReportada;

    private String observaciones;
}
