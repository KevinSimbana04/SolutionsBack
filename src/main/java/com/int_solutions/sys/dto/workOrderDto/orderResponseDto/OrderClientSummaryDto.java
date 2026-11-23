package com.int_solutions.sys.dto.workOrderDto.orderResponseDto;

import com.int_solutions.sys.entity.workOrder.OrderStatusE;

import java.time.LocalDate;

public record OrderClientSummaryDto(
        Long ordenId,
        String codigo,
        OrderStatusE estadoOrden,
        String  equipoResumen,
        LocalDate fechaCreacion,
        LocalDate fechaEstimadaEntrega,
        LocalDate fechaDeEntrega
) {
}
