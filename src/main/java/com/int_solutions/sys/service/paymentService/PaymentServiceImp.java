package com.int_solutions.sys.service.paymentService;

import com.int_solutions.sys.dto.filterDto.OrderFilterDto;
import com.int_solutions.sys.dto.filterDto.PaymentFilterDto;
import com.int_solutions.sys.dto.paymentDto.*;
import com.int_solutions.sys.entity.auth.RoleE;
import com.int_solutions.sys.entity.budget.Budget;
import com.int_solutions.sys.entity.payment.Payment;
import com.int_solutions.sys.entity.payment.PaymentMethodE;
import com.int_solutions.sys.entity.payment.PaymentStatusE;
import com.int_solutions.sys.entity.user.Customer;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.entity.workOrder.Order;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import com.int_solutions.sys.mapper.PaymentMapper;
import com.int_solutions.sys.repository.BudgetRepository;
import com.int_solutions.sys.repository.OrderRepository;
import com.int_solutions.sys.repository.PaymentRepository;
import com.int_solutions.sys.repository.specification.OrderSpecification;
import com.int_solutions.sys.repository.specification.PaymentSpecification;
import com.int_solutions.sys.util.SecurityContextHelper;
import com.int_solutions.sys.util.order.OrderAuditHelper;
import com.int_solutions.sys.util.payment.PaymentReceiptPdfHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImp implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BudgetRepository budgetRepository;
    private final OrderRepository orderRepository;
    private final SecurityContextHelper securityContextHelper;
    private final OrderAuditHelper orderAuditHelper;
    private final PaymentMapper paymentMapper;
    private final PaymentReceiptPdfHelper pdfHelper;

    @Override
    @Transactional
    public PaymentTransactionResponseDto registrarPago(Long ordenId, PaymentCreateRequestDto request) {
        User usuario = securityContextHelper.obtenerUsuarioEnSesion();
        Order orden = orderAuditHelper.buscarOrden(ordenId);

        Budget presupuesto = budgetRepository.findByOrdenTrabajo_OrdenId(ordenId)
                .orElseThrow(() -> new IllegalStateException("La orden no tiene un presupuesto generado."));

        if (!"APROBADO".equals(presupuesto.getEstadoPresupuesto())) {
            throw new IllegalStateException("Solo se pueden registrar pagos en órdenes con presupuesto aprobado.");
        }

        if (request.getMetodoPago() == PaymentMethodE.TRANSFERENCIA
                && (request.getReferenciaBancaria() == null || request.getReferenciaBancaria().trim().length() < 5)) {
            throw new IllegalArgumentException("El número de referencia o transacción bancaria es obligatorio para pagos por transferencia.");
        }

        BigDecimal totalAbonado = paymentRepository.sumarTotalAbonadoActivo(ordenId);
        BigDecimal saldoPendiente = presupuesto.getTotalPagar().subtract(totalAbonado);

        if (saldoPendiente.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Esta orden ya se encuentra pagada en su totalidad.");
        }
        if (request.getMonto().compareTo(saldoPendiente) > 0) {
            throw new IllegalArgumentException(
                    "El monto ingresado excede el saldo pendiente de la orden. El valor máximo permitido es $" + saldoPendiente
            );
        }

        Payment guardado = paymentRepository.save(
                paymentMapper.toEntity(request, generarNumeroRecibo(), orden, usuario)
        );

        orderAuditHelper.registrarHistorial(
                orden,
                orden.getEstadoOrden(),
                orden.getEstadoOrden(),
                usuario,
                "Pago registrado (" + guardado.getNumeroRecibo() + ") por $" + guardado.getMonto() + " vía " + guardado.getMetodoPago()
        );

        return paymentMapper.toTransactionDto(guardado);
    }

    @Override
    @Transactional
    public PaymentTransactionResponseDto anularPago(Long pagoId, PaymentCancelRequestDto request) {
        User admin = securityContextHelper.obtenerUsuarioEnSesion();
        Payment pago = paymentRepository.findById(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado."));

        if (pago.getEstadoPago() == PaymentStatusE.ANULADO) {
            throw new IllegalStateException("Este pago ya fue anulado previamente.");
        }

        Order orden = pago.getOrdenTrabajo();
        if (orden.getEstadoOrden().getTipoEstado() == OrderStatusE.ENTREGADO) {
            throw new IllegalStateException("No se puede anular un pago de una orden que ya fue entregada y cerrada.");
        }

        pago.setEstadoPago(PaymentStatusE.ANULADO);
        pago.setMotivoAnulacion(request.getMotivoAnulacion().trim());
        pago.setFechaAnulacion(LocalDateTime.now());
        pago.setUsuarioAnulacion(admin);

        Payment anulado = paymentRepository.save(pago);

        orderAuditHelper.registrarHistorial(
                orden,
                orden.getEstadoOrden(),
                orden.getEstadoOrden(),
                admin,
                "Anulación de pago " + pago.getNumeroRecibo() + " ($" + pago.getMonto() + "). Motivo: " + request.getMotivoAnulacion()
        );

        return paymentMapper.toTransactionDto(anulado);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDashboardResponseDto consultarCobrosAdmin(PaymentFilterDto filtro) {
        if (filtro.fechaInicio() != null && filtro.fechaFin() != null
                && filtro.fechaInicio().isAfter(filtro.fechaFin())) {
            throw new IllegalArgumentException("El rango de fechas es inválido.");
        }

        boolean hayFiltroTransaccion = filtro.fechaInicio() != null
                || filtro.fechaFin() != null
                || filtro.metodoPago() != null;

        List<Budget> presupuestosAprobados = budgetRepository.findByEstadoPresupuesto("APROBADO");
        Map<Long, OrderBalanceSummaryDto> resumenPorOrden = new LinkedHashMap<>();

        if (hayFiltroTransaccion) {
            List<Payment> pagosFiltrados = paymentRepository.findAll(
                    PaymentSpecification.conFiltros(filtro),
                    Sort.by(Sort.Order.desc("fechaPago"))
            );
            Set<Long> ordenesIds = new LinkedHashSet<>();
            pagosFiltrados.forEach(p -> ordenesIds.add(p.getOrdenTrabajo().getOrdenId()));

            for (Long ordenId : ordenesIds) {
                resumenPorOrden.put(ordenId, construirResumenOrden(ordenId));
            }
        } else {
            for (Budget b : presupuestosAprobados) {
                Order o = b.getOrdenTrabajo();
                if (filtro.estadoOrden() != null && o.getEstadoOrden().getTipoEstado() != filtro.estadoOrden()) {
                    continue;
                }
                if (filtro.busqueda() != null && !filtro.busqueda().isBlank()) {
                    String term = filtro.busqueda().trim().toLowerCase();
                    boolean coincide = o.getCodigo().toLowerCase().contains(term)
                            || o.getCliente().getIdentificacion().toLowerCase().contains(term)
                            || (o.getCliente().getNombres() + " " + o.getCliente().getApellidos()).toLowerCase().contains(term);
                    if (!coincide) continue;
                }
                resumenPorOrden.put(o.getOrdenId(), construirResumenOrden(o.getOrdenId()));
            }
        }

        List<OrderBalanceSummaryDto> listaFinal = resumenPorOrden.values().stream()
                .filter(r -> filtro.estadoFinanciero() == null || filtro.estadoFinanciero().isBlank()
                        || r.estadoPago().equalsIgnoreCase(filtro.estadoFinanciero()))
                .toList();

        BigDecimal totalRecaudado = listaFinal.stream()
                .map(OrderBalanceSummaryDto::totalAbonado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaldos = listaFinal.stream()
                .map(OrderBalanceSummaryDto::saldoPendiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long ordenesConDeuda = listaFinal.stream()
                .filter(r -> r.saldoPendiente().compareTo(BigDecimal.ZERO) > 0)
                .count();

        return new PaymentDashboardResponseDto(totalRecaudado, totalSaldos, ordenesConDeuda, listaFinal);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderBalanceSummaryDto obtenerResumenOrden(Long ordenId) {
        return construirResumenOrden(ordenId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientPaymentStateDto> listarMisPagosCliente() {
        Customer cliente = securityContextHelper.obtenerClienteEnSesion();
        List<Order> ordenes = orderRepository.findAll(
                OrderSpecification.conFiltros(OrderFilterDto.paraCliente(cliente.getClienteId())),
                Sort.by(Sort.Order.desc("fechaCreacion"))
        );

        List<ClientPaymentStateDto> resultado = new ArrayList<>();
        for (Order orden : ordenes) {
            budgetRepository.findByOrdenTrabajo_OrdenId(orden.getOrdenId())
                    .filter(b -> "APROBADO".equals(b.getEstadoPresupuesto()))
                    .ifPresent(b -> resultado.add(construirVistaCliente(orden, b)));
        }
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public ClientPaymentStateDto obtenerMisPagosPorOrden(Long ordenId) {
        Customer cliente = securityContextHelper.obtenerClienteEnSesion();
        Order orden = orderAuditHelper.buscarOrden(ordenId);

        if (!orden.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new AccessDeniedException("No tiene autorización para visualizar los comprobantes de esta orden.");
        }

        Budget presupuesto = budgetRepository.findByOrdenTrabajo_OrdenId(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("No existe presupuesto para esta orden."));

        return construirVistaCliente(orden, presupuesto);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] descargarComprobantePdf(Long pagoId) {
        User usuario = securityContextHelper.obtenerUsuarioEnSesion();
        Payment pago = paymentRepository.findById(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Comprobante de pago no encontrado."));

        boolean esCliente = usuario.getRoles().stream().anyMatch(r -> r.getNombreRol() == RoleE.CLIENTE);
        if (esCliente) {
            Customer cliente = securityContextHelper.obtenerClienteEnSesion();
            if (!pago.getOrdenTrabajo().getCliente().getClienteId().equals(cliente.getClienteId())) {
                throw new AccessDeniedException("No tiene autorización para visualizar los comprobantes de esta orden.");
            }
        }

        Long ordenId = pago.getOrdenTrabajo().getOrdenId();
        Budget presupuesto = budgetRepository.findByOrdenTrabajo_OrdenId(ordenId)
                .orElseThrow(() -> new IllegalStateException("Presupuesto no encontrado."));
        BigDecimal abonado = paymentRepository
                .findByOrdenTrabajo_OrdenIdAndEstadoPagoOrderByFechaPagoAsc(ordenId, PaymentStatusE.ACTIVO)
                .stream()
                .filter(p -> p.getPagoId() <= pago.getPagoId())
                .map(Payment::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = presupuesto.getTotalPagar().subtract(abonado);

        return pdfHelper.generarRecibo(pago, presupuesto.getTotalPagar(), abonado, saldo);
    }

    private OrderBalanceSummaryDto construirResumenOrden(Long ordenId) {
        Order orden = orderAuditHelper.buscarOrden(ordenId);
        Budget presupuesto = budgetRepository.findByOrdenTrabajo_OrdenId(ordenId)
                .orElseThrow(() -> new IllegalStateException("Presupuesto no encontrado para la orden."));

        List<Payment> pagos = paymentRepository.findByOrdenTrabajo_OrdenIdOrderByFechaPagoDesc(ordenId);
        BigDecimal totalAbonado = pagos.stream()
                .filter(p -> p.getEstadoPago() == PaymentStatusE.ACTIVO)
                .map(Payment::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = presupuesto.getTotalPagar().subtract(totalAbonado);
        String estadoFinanciero = resolverEstadoFinanciero(totalAbonado, saldo);
        LocalDateTime ultimoPago = pagos.isEmpty() ? null : pagos.get(0).getFechaPago();

        return paymentMapper.toOrderBalanceSummaryDto(
                orden, presupuesto, totalAbonado, saldo, estadoFinanciero, ultimoPago, pagos
        );
    }

    private ClientPaymentStateDto construirVistaCliente(Order orden, Budget presupuesto) {
        List<Payment> activos = paymentRepository.findByOrdenTrabajo_OrdenIdAndEstadoPagoOrderByFechaPagoAsc(
                orden.getOrdenId(), PaymentStatusE.ACTIVO
        );
        BigDecimal abonado = activos.stream()
                .map(Payment::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldo = presupuesto.getTotalPagar().subtract(abonado);

        return paymentMapper.toClientPaymentStateDto(
                orden, presupuesto, abonado, saldo, resolverEstadoFinanciero(abonado, saldo), activos
        );
    }

    private String resolverEstadoFinanciero(BigDecimal abonado, BigDecimal saldo) {
        if (saldo.compareTo(BigDecimal.ZERO) <= 0) return "PAGADO";
        if (abonado.compareTo(BigDecimal.ZERO) > 0) return "PAGO_PARCIAL";
        return "PENDIENTE";
    }

    private String generarNumeroRecibo() {
        String prefijo = "REC-" + LocalDate.now().getYear() + "-";
        long correlativo = paymentRepository.countByNumeroReciboStartingWith(prefijo) + 1;
        return prefijo + String.format("%04d", correlativo);
    }
}
