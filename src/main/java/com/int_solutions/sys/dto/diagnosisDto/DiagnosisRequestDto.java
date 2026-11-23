package com.int_solutions.sys.dto.diagnosisDto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisRequestDto {

    @NotBlank(message = "La falla técnica confirmada es obligatoria")
    @Size(min = 10, message = "La falla técnica debe tener al menos 10 caracteres")
    private String causaProbableTecnico;

    @NotBlank(message = "Las pruebas y mediciones efectuadas son obligatorias")
    @Size(min = 10, message = "Detalle las pruebas realizadas (mínimo 10 caracteres)")
    private String pruebasRealizadas;

    @NotBlank(message = "El dictamen de viabilidad es obligatorio")
    @Pattern(regexp = "^(TIENE_REPARACION|SIN_REPARACION)$",
            message = "El dictamen debe ser TIENE_REPARACION o SIN_REPARACION")
    private String posibilidadesReparacion;

    private String repuestosRequeridos;

    @DecimalMin(value = "0.00", message = "El valor estimado de mano de obra debe ser positivo")
    private BigDecimal valorManoObra;

    @Min(value = 1, message = "El tiempo estimado mínimo es 1 día hábil")
    @Max(value = 30, message = "El tiempo estimado máximo es 30 días hábiles")
    private Integer tiempoEstimado;

    @Size(min = 15, message = "La justificación de inviabilidad debe tener al menos 15 caracteres")
    private String justificacionInvalida;

    private String evidenciaFotografica;
    private String observacionesTecnico;
}