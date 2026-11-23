package com.int_solutions.sys.dto.workOrderDto.orderResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponseDto (
        OrderResponseDto datosGenerales,
        LocalDateTime fechaCreacion,
        String administrativoNombreCompleto,
        String accesorios,
        String motivoCancelacion,
        List<OrderHistoryResponseDto> historial
) {
}
