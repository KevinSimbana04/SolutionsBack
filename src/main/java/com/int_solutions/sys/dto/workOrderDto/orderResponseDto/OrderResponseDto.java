package com.int_solutions.sys.dto.workOrderDto.orderResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderResponseDto(
        Long ordenId,
        String codigo,
        String estadoOrden,
        LocalDate fechaEstimadaEntrega,
        LocalDateTime fechaDeEntrega,


        Long clienteId,
        String clienteNombreCompleto,

        Long equipoId,
        String equipoResumen,
        String fallaReportada,

        Long tecnicoId,
        String tecnicoNombreCompleto
) {
}
