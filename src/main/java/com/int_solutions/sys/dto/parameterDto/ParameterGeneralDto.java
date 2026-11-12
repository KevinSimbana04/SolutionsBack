package com.int_solutions.sys.dto.parameterDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterGeneralDto {

    private static final String MSG_RANGO = "El valor ingresado no es válido. Debe ser un número positivo dentro del rango establecido";

    @NotNull(message = MSG_RANGO)
    @Min(value = 0, message = MSG_RANGO)
    @Max(value = 100, message = MSG_RANGO)
    private Integer iva;

    @NotNull(message = MSG_RANGO)
    @Min(value = 1, message = MSG_RANGO)
    @Max(value = 30, message = MSG_RANGO)
    private Integer tiempoGarantia;

    @NotNull(message = MSG_RANGO)
    @Min(value = 1, message = MSG_RANGO)
    @Max(value = 5, message = MSG_RANGO)
    private Integer tiempoMaximoEntrega;

    @NotNull(message = MSG_RANGO)
    @Min(value = 1, message = MSG_RANGO)
    @Max(value = 15, message = MSG_RANGO)
    private Integer tiempoLimiteDiagnostico;

    @NotNull(message = MSG_RANGO)
    @Min(value = 1, message = MSG_RANGO)
    @Max(value = 3, message = MSG_RANGO)
    private Integer tiempoLimitePresupuesto;

    private String mensaje;
}
