package com.int_solutions.sys.dto.paymentDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ClientPaymentStateDto(
        Long ordenId,
        String codigoOrden,
        String equipoResumen,
        BigDecimal totalPresupuestado,
        BigDecimal totalAbonado,
        BigDecimal saldoPendiente,
        String estadoPago,
        List<ClientAbonoDto> abonos
) {
    public record ClientAbonoDto(
            Long pagoId,
            String numeroRecibo,
            LocalDateTime fechaPago,
            String metodoPago,
            BigDecimal monto,
            String referenciaBancaria
    ) {}
}