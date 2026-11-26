package com.int_solutions.sys.dto.filterDto;

import com.int_solutions.sys.entity.payment.PaymentMethodE;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;

import java.time.LocalDate;

public record PaymentFilterDto(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        PaymentMethodE metodoPago,
        OrderStatusE estadoOrden,
        String estadoFinanciero, // PENDIENTE | PAGO_PARCIAL | PAGADO
        String busqueda
) {
}
