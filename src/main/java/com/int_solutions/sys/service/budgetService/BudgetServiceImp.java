package com.int_solutions.sys.service.budgetService;


import com.int_solutions.sys.dto.budgetDto.ApprovedSparePartDto;
import com.int_solutions.sys.dto.budgetDto.BudgetCreateRequestDto;
import com.int_solutions.sys.dto.budgetDto.BudgetRespondRequestDto;
import com.int_solutions.sys.dto.budgetDto.BudgetResponseDto;
import com.int_solutions.sys.entity.auth.RoleE;
import com.int_solutions.sys.entity.budget.Budget;
import com.int_solutions.sys.entity.budget.DetailSparePart;
import com.int_solutions.sys.entity.config.Parameter;
import com.int_solutions.sys.entity.diagnosis.Diagnosis;
import com.int_solutions.sys.entity.user.Customer;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.entity.workOrder.Order;
import com.int_solutions.sys.entity.workOrder.OrderStatus;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import com.int_solutions.sys.mapper.BudgetMapper;
import com.int_solutions.sys.repository.*;
import com.int_solutions.sys.util.SecurityContextHelper;
import com.int_solutions.sys.util.order.OrderAuditHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetServiceImp implements  BudgetService{
    private final BudgetRepository budgetRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final SecurityContextHelper securityContextHelper;
    private final OrderAuditHelper orderAuditHelper;
    private final BudgetMapper budgetMapper;
    private final ParameterRepository parameterRepository;

    @Override
    @Transactional
    public BudgetResponseDto emitirPresupuesto(Long ordenId, BudgetCreateRequestDto request) {
        User usuarioSesion = securityContextHelper.obtenerUsuarioEnSesion();
        Order orden = orderAuditHelper.buscarOrden(ordenId);

        if (orden.getEstadoOrden().getTipoEstado() != OrderStatusE.PENDIENTE_PRESUPUESTO) {
            throw new IllegalStateException("La orden debe estar en estadoOrden PENDIENTE_PRESUPUESTO para emitir presupuesto.");
        }

        if (budgetRepository.findByOrdenTrabajo_OrdenId(ordenId).isPresent()) {
            throw new IllegalStateException("Esta orden ya tiene un presupuesto generado.");
        }

        Diagnosis diagnostico = diagnosisRepository.findByOrdenTrabajo_OrdenId(ordenId)
                .orElseThrow(() -> new IllegalStateException("No existe un diagnóstico técnico emitido para esta orden."));

        int diasLimite = obtenerParametroEntero("TIEMPO_LIMITE_PRESUPUESTO", 3);
        LocalDateTime ahora = LocalDateTime.now();

        Budget presupuesto = budgetMapper.toEntity(request, orden, diagnostico, ahora, ahora.plusDays(diasLimite));
        calcularTotalesYRepuestos(presupuesto, request);

        Budget guardado = budgetRepository.save(presupuesto);

        OrderStatus estadoAnterior = orden.getEstadoOrden();
        OrderStatus estadoEnviado = orderAuditHelper.obtenerEstado(OrderStatusE.PRESUPUESTO_ENVIADO);
        orden.setEstadoOrden(estadoEnviado);

        orderAuditHelper.registrarHistorial(
                orden,
                estadoAnterior,
                estadoEnviado,
                usuarioSesion,
                "Presupuesto comercial enviado al cliente por un total de $" + guardado.getTotalPagar()
        );

        return budgetMapper.toResponseDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponseDto consultarPresupuesto(Long ordenId) {
        User usuarioSesion = securityContextHelper.obtenerUsuarioEnSesion();
        Budget presupuesto = buscarPresupuesto(ordenId);

        boolean esCliente = usuarioSesion.getRoles().stream()
                .anyMatch(r -> r.getNombreRol() == RoleE.CLIENTE);

        if (esCliente) {
            Customer cliente = securityContextHelper.obtenerClienteEnSesion();
            if (!presupuesto.getOrdenTrabajo().getCliente().getClienteId().equals(cliente.getClienteId())) {
                throw new AccessDeniedException("No tiene autorización para visualizar el presupuesto de esta orden.");
            }
        }

        return budgetMapper.toResponseDto(presupuesto);
    }


    @Override
    @Transactional
    public BudgetResponseDto responderPresupuesto(Long ordenId, BudgetRespondRequestDto request) {
        Customer cliente = securityContextHelper.obtenerClienteEnSesion();
        Order orden = orderAuditHelper.buscarOrden(ordenId);

        if (!orden.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new AccessDeniedException("No tiene autorización para responder el presupuesto de esta orden.");
        }

        if (orden.getEstadoOrden().getTipoEstado() != OrderStatusE.PRESUPUESTO_ENVIADO) {
            throw new IllegalStateException("El presupuesto ya fue respondido o no está disponible para aprobación.");
        }

        Budget presupuesto = buscarPresupuesto(ordenId);
        OrderStatus estadoAnterior = orden.getEstadoOrden();
        OrderStatus nuevoEstado;
        String notaHistorial;

        if ("APROBAR".equals(request.getDecision())) {
            if (!Boolean.TRUE.equals(request.getConfirmacion())) {
                throw new IllegalArgumentException("Debe marcar la casilla de confirmación digital para aprobar el presupuesto.");
            }
            presupuesto.setConfirmacion(true);
            presupuesto.setEstadoPresupuesto("APROBADO");
            nuevoEstado = orderAuditHelper.obtenerEstado(OrderStatusE.PRESUPUESTO_APROBADO);
            notaHistorial = "Cliente aprobó el presupuesto en línea.";
        } else {
            String motivoFinal = resolverMotivoRechazo(request);
            presupuesto.setEstadoPresupuesto("RECHAZADO");
            presupuesto.setMotivoRechazo(motivoFinal);
            nuevoEstado = orderAuditHelper.obtenerEstado(OrderStatusE.PRESUPUESTO_RECHAZADO);
            notaHistorial = "Cliente rechazó el presupuesto. Motivo: " + motivoFinal;
        }

        orden.setEstadoOrden(nuevoEstado);
        orderAuditHelper.registrarHistorial(orden, estadoAnterior, nuevoEstado, cliente.getUsuario(), notaHistorial);

        return budgetMapper.toResponseDto(budgetRepository.save(presupuesto));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ApprovedSparePartDto> listarRepuestosAprobados(Long ordenId) {
        Employee tecnico = securityContextHelper.obtenerEmpleadoEnSesion();
        Budget presupuesto = buscarPresupuesto(ordenId);

        orderAuditHelper.validarTecnicoAsignado(presupuesto.getOrdenTrabajo(), tecnico);

        if (!"APROBADO".equals(presupuesto.getEstadoPresupuesto())) {
            throw new IllegalStateException("El presupuesto de esta orden aún no ha sido aprobado por el cliente.");
        }

        return budgetMapper.toApprovedSparePartDtoList(presupuesto.getDetallesRepuesto());
    }


    private Budget buscarPresupuesto(Long ordenId) {
        return budgetRepository.findByOrdenTrabajo_OrdenId(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("No existe presupuesto generado para esta orden."));
    }

    private void calcularTotalesYRepuestos(Budget presupuesto, BudgetCreateRequestDto request) {
        List<DetailSparePart> items = request.getRepuestos() == null ? List.of()
                : request.getRepuestos().stream()
                .map(item -> budgetMapper.toSparePartEntity(item, presupuesto))
                .toList();

        BigDecimal costoRepuestos = items.stream()
                .map(i -> i.getCostoUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal porcentajeIva = obtenerPorcentajeIva();
        BigDecimal subtotal = request.getCostoManoObra().add(costoRepuestos);
        BigDecimal valorIva = subtotal.multiply(porcentajeIva).setScale(2, RoundingMode.HALF_UP);

        presupuesto.setCostoRepuestos(costoRepuestos);
        presupuesto.setSubtotal(subtotal);
        presupuesto.setValorIva(valorIva);
        presupuesto.setTotalPagar(subtotal.add(valorIva));
        presupuesto.setDetallesRepuesto(items);
    }

    private String resolverMotivoRechazo(BudgetRespondRequestDto request) {
        if (request.getMotivoRechazo() == null || request.getMotivoRechazo().isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar un motivo de rechazo.");
        }
        if ("OTRO".equalsIgnoreCase(request.getMotivoRechazo())
                && (request.getComentarioAdicional() == null || request.getComentarioAdicional().trim().length() < 5)) {
            throw new IllegalArgumentException("Debe ingresar un comentario adicional de 5 a 200 caracteres cuando el motivo es OTRO.");
        }
        return request.getMotivoRechazo()
                + (request.getComentarioAdicional() != null ? " - " + request.getComentarioAdicional() : "");
    }

    private int obtenerParametroEntero(String clave, int defecto) {
        return parameterRepository.findByClave(clave)
                .map(p -> Integer.parseInt(p.getValor()))
                .orElse(defecto);
    }

    private BigDecimal obtenerPorcentajeIva() {
        String ivaValor = parameterRepository.findByClave("IVA")
                .map(Parameter::getValor)
                .orElse("15");
        return new BigDecimal(ivaValor).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
    }
}