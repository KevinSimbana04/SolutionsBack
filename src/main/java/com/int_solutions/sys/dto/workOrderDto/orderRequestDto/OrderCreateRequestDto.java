package com.int_solutions.sys.dto.workOrderDto.orderRequestDto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestDto {
    @NotNull(message = "El cliente es obligatorio")
    @Positive(message = "El identificador del cliente no es válido")
    private Long clienteId;

    @NotNull(message = "El equipo es obligatorio")
    @Positive(message = "El identificador del equipo no es válido")
    private Long equipoId;

    @NotNull(message = "Debe seleccionar un técnico asignado")
    @Positive(message = "El identificador del técnico no es válido")
    private Long tecnicoId;

    @NotNull(message = "La fecha estimada de entrega es obligatoria")
    @FutureOrPresent(message = "La fecha estimada de entrega no puede ser anterior a hoy")
    private LocalDate fechaEstimadaEntrega;
}
