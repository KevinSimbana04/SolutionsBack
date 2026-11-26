package com.int_solutions.sys.controller;

import com.int_solutions.sys.dto.filterDto.PaymentFilterDto;
import com.int_solutions.sys.dto.paymentDto.*;
import com.int_solutions.sys.entity.payment.PaymentMethodE;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import com.int_solutions.sys.service.paymentService.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/admin")
    @PreAuthorize(" hasAuthority('PAGO_CONSULTAR_TODOS') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<PaymentDashboardResponseDto> consultarCobrosAdmin(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) PaymentMethodE metodoPago,
            @RequestParam(required = false) OrderStatusE estadoOrden,
            @RequestParam(required = false) String estadoFinanciero,
            @RequestParam(required = false) String busqueda) {

        PaymentFilterDto filtro = new PaymentFilterDto(
                fechaInicio, fechaFin, metodoPago, estadoOrden, estadoFinanciero, busqueda
        );
        return ResponseEntity.ok(paymentService.consultarCobrosAdmin(filtro));
    }

    @PostMapping("/ordenes/{ordenId}")
    @PreAuthorize(" hasAuthority('PAGO_REGISTRAR') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<PaymentTransactionResponseDto> registrarPago(
            @PathVariable Long ordenId,
            @Valid @RequestBody PaymentCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.registrarPago(ordenId, request));
    }

    @PatchMapping("/{pagoId}/anular")
    @PreAuthorize(" hasAuthority('PAGO_ANULAR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<PaymentTransactionResponseDto> anularPago(
            @PathVariable Long pagoId,
            @Valid @RequestBody PaymentCancelRequestDto request) {
        return ResponseEntity.ok(paymentService.anularPago(pagoId, request));
    }

    @GetMapping("/ordenes/{ordenId}")
    @PreAuthorize("hasAnyAuthority('PAGO_CONSULTAR_TODOS', 'PAGO_REGISTRAR') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<OrderBalanceSummaryDto> obtenerResumenOrden(@PathVariable Long ordenId) {
        return ResponseEntity.ok(paymentService.obtenerResumenOrden(ordenId));
    }

    @GetMapping("/cliente/mis-pagos")
    @PreAuthorize("hasAuthority('PAGO_CONSULTAR_PROPIOS') or hasRole('CLIENTE')")
    public ResponseEntity<List<ClientPaymentStateDto>> listarMisPagosCliente() {
        return ResponseEntity.ok(paymentService.listarMisPagosCliente());
    }

    @GetMapping("/cliente/ordenes/{ordenId}")
    @PreAuthorize("hasAuthority('PAGO_CONSULTAR_PROPIOS') or hasRole('CLIENTE')")
    public ResponseEntity<ClientPaymentStateDto> obtenerMisPagosPorOrden(@PathVariable Long ordenId) {
        return ResponseEntity.ok(paymentService.obtenerMisPagosPorOrden(ordenId));
    }

    @GetMapping("/{pagoId}/comprobante")
    @PreAuthorize(" hasAuthority('PAGO_DESCARGAR_COMPROBANTE') or hasAnyRole('CLIENTE', 'ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<byte[]> descargarComprobantePdf(@PathVariable Long pagoId) {
        byte[] pdf = paymentService.descargarComprobantePdf(pagoId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"comprobante-pago-" + pagoId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
