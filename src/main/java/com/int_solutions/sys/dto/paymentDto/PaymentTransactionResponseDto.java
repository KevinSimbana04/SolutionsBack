package com.int_solutions.sys.dto.paymentDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentTransactionResponseDto(
        Long pagoId,
        String numeroRecibo,
        BigDecimal monto,
        String metodoPago,
        String referenciaBancaria,
        String comprobanteUrl,
        String observaciones,
        String estadoPago,
        String motivoAnulacion,
        LocalDateTime fechaPago,
        LocalDateTime fechaAnulacion,
        String usuarioRegistro
) {
}