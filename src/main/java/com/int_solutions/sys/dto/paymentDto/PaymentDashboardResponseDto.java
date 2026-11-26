package com.int_solutions.sys.dto.paymentDto;

import java.math.BigDecimal;
import java.util.List;

public record PaymentDashboardResponseDto(
        BigDecimal totalRecaudado,
        BigDecimal totalSaldosPendientes,
        long cantidadOrdenesConDeuda,
        List<OrderBalanceSummaryDto> ordenes
) {
}