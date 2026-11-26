package com.int_solutions.sys.service.paymentService;

import com.int_solutions.sys.dto.filterDto.PaymentFilterDto;
import com.int_solutions.sys.dto.paymentDto.*;

import java.util.List;

public interface PaymentService {
    PaymentTransactionResponseDto registrarPago(Long ordenId, PaymentCreateRequestDto request);
    PaymentTransactionResponseDto anularPago(Long pagoId, PaymentCancelRequestDto request);
    PaymentDashboardResponseDto consultarCobrosAdmin(PaymentFilterDto filtro);
    OrderBalanceSummaryDto obtenerResumenOrden(Long ordenId);


    List<ClientPaymentStateDto> listarMisPagosCliente();
    ClientPaymentStateDto obtenerMisPagosPorOrden(Long ordenId);
    byte[] descargarComprobantePdf(Long pagoId);
}
