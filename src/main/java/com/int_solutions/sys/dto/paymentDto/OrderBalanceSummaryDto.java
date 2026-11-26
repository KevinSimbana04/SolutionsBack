package com.int_solutions.sys.dto.paymentDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderBalanceSummaryDto(
        Long ordenId,
        String codigoOrden,
        String clienteNombreCompleto,
        String equipoResumen,
        String estadoOrden,
        BigDecimal totalPresupuestado,
        BigDecimal totalAbonado,
        BigDecimal saldoPendiente,
        String estadoPago,
        LocalDateTime fechaUltimoPago,
        List<PaymentTransactionResponseDto> pagos
) {
}