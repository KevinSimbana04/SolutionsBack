package com.int_solutions.sys.mapper;

import com.int_solutions.sys.dto.paymentDto.ClientPaymentStateDto;
import com.int_solutions.sys.dto.paymentDto.OrderBalanceSummaryDto;
import com.int_solutions.sys.dto.paymentDto.PaymentCreateRequestDto;
import com.int_solutions.sys.dto.paymentDto.PaymentTransactionResponseDto;
import com.int_solutions.sys.entity.budget.Budget;
import com.int_solutions.sys.entity.payment.Payment;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.entity.workOrder.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    //Datos de entrada
    @Mapping(target = "pagoId", ignore = true)
    @Mapping(target = "estadoPago", ignore = true)
    @Mapping(target = "motivoAnulacion", ignore = true)
    @Mapping(target = "fechaPago", ignore = true)
    @Mapping(target = "fechaAnulacion", ignore = true)
    @Mapping(target = "usuarioAnulacion", ignore = true)
    @Mapping(source = "numeroRecibo", target = "numeroRecibo")
    @Mapping(source = "orden", target = "ordenTrabajo")
    @Mapping(source = "usuario", target = "usuarioRegistro")
    Payment toEntity(PaymentCreateRequestDto request, String numeroRecibo, Order orden, User usuario);

    @Mapping(source = "orden.ordenId", target = "ordenId")
    @Mapping(source = "orden.codigo", target = "codigoOrden")
    @Mapping(target = "clienteNombreCompleto", expression = "java(orden.getCliente().getNombres() + \" \" + orden.getCliente().getApellidos())")
    @Mapping(target = "equipoResumen", expression = "java(orden.getEquipo().getMarca() + \" \" + orden.getEquipo().getModelo())")
    @Mapping(target = "estadoOrden", expression = "java(orden.getEstadoOrden().getTipoEstado().name())")
    @Mapping(source = "presupuesto.totalPagar", target = "totalPresupuestado")
    @Mapping(source = "totalAbonado", target = "totalAbonado")
    @Mapping(source = "saldo", target = "saldoPendiente")
    @Mapping(source = "estadoFinanciero", target = "estadoPago")
    @Mapping(source = "ultimoPago", target = "fechaUltimoPago")
    @Mapping(source = "pagos", target = "pagos")
    OrderBalanceSummaryDto toOrderBalanceSummaryDto(
            Order orden,
            Budget presupuesto,
            BigDecimal totalAbonado,
            BigDecimal saldo,
            String estadoFinanciero,
            LocalDateTime ultimoPago,
            List<Payment> pagos
    );

    @Mapping(source = "orden.ordenId", target = "ordenId")
    @Mapping(source = "orden.codigo", target = "codigoOrden")
    @Mapping(target = "equipoResumen", expression = "java(orden.getEquipo().getMarca() + \" \" + orden.getEquipo().getModelo())")
    @Mapping(source = "presupuesto.totalPagar", target = "totalPresupuestado")
    @Mapping(source = "abonado", target = "totalAbonado")
    @Mapping(source = "saldo", target = "saldoPendiente")
    @Mapping(source = "estadoFinanciero", target = "estadoPago")
    @Mapping(source = "activos", target = "abonos")
    ClientPaymentStateDto toClientPaymentStateDto(
            Order orden,
            Budget presupuesto,
            BigDecimal abonado,
            BigDecimal saldo,
            String estadoFinanciero,
            List<Payment> activos
    );

    //Datos de salida
    @Mapping(source = "usuarioRegistro.nombreUsuario", target = "usuarioRegistro")
    PaymentTransactionResponseDto toTransactionDto(Payment payment);
    ClientPaymentStateDto.ClientAbonoDto toClientAbonoDto(Payment payment);

}
