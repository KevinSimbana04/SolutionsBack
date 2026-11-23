package com.int_solutions.sys.service.diagnosisService;

import com.int_solutions.sys.dto.diagnosisDto.DiagnosisRequestDto;
import com.int_solutions.sys.dto.diagnosisDto.DiagnosisResponseDto;
import com.int_solutions.sys.entity.diagnosis.Diagnosis;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.entity.workOrder.Order;
import com.int_solutions.sys.entity.workOrder.OrderStatus;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import com.int_solutions.sys.mapper.DiagnosisMapper;
import com.int_solutions.sys.repository.DiagnosisRepository;
import com.int_solutions.sys.repository.EmployeeRepository;
import com.int_solutions.sys.repository.OrderRepository;
import com.int_solutions.sys.util.SecurityContextHelper;
import com.int_solutions.sys.util.order.OrderAuditHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DiagnosisServiceImp implements DiagnosisService{
    private final DiagnosisRepository diagnosisRepository;
    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    private final SecurityContextHelper securityContextHelper;
    private final OrderAuditHelper orderAuditHelper;
    private final DiagnosisMapper diagnosisMapper;

    @Override
    @Transactional
    public DiagnosisResponseDto emitirDiagnostico(Long ordenId, DiagnosisRequestDto request) {
        Employee tecnico = securityContextHelper.obtenerEmpleadoEnSesion();
        Order orden = orderAuditHelper.buscarOrden(ordenId);
        orderAuditHelper.validarTecnicoAsignado(orden, tecnico);

        OrderStatusE estadoActual = orden.getEstadoOrden().getTipoEstado();
        if (!Set.of(OrderStatusE.RECIBIDO_POR_TECNICO, OrderStatusE.EN_DIAGNOSTICO).contains(estadoActual)) {
            throw new IllegalStateException("La orden debe estar en estadoOrden RECIBIDO_POR_TECNICO o EN_DIAGNOSTICO.");
        }

        if (diagnosisRepository.existsByOrdenTrabajo_OrdenId(ordenId)) {
            throw new IllegalStateException("Esta orden ya cuenta con un diagnóstico emitido.");
        }

        boolean tieneReparacion = "TIENE_REPARACION".equals(request.getPosibilidadesReparacion());
        validarReglasDiagnostico(request, tieneReparacion);

        Diagnosis guardado = diagnosisRepository.save(
                diagnosisMapper.toEntity(request, orden, tecnico)
        );

        OrderStatus estadoAnterior = orden.getEstadoOrden();
        OrderStatusE nuevoEnum = tieneReparacion ? OrderStatusE.PENDIENTE_PRESUPUESTO : OrderStatusE.SIN_REPARACION;
        OrderStatus nuevoEstado = orderAuditHelper.obtenerEstado(nuevoEnum);

        orden.setEstadoOrden(nuevoEstado);
        orderAuditHelper.registrarHistorial(
                orden,
                estadoAnterior,
                nuevoEstado,
                tecnico.getUsuario(),
                "Diagnóstico emitido: " + request.getPosibilidadesReparacion()
        );

        return diagnosisMapper.toResponseDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public DiagnosisResponseDto obtenerPorOrden(Long ordenId) {
        Diagnosis diagnostico = diagnosisRepository.findByOrdenTrabajo_OrdenId(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("No existe diagnóstico para la orden " + ordenId));

        orderAuditHelper.validarLecturaOrden(diagnostico.getOrdenTrabajo());

        return diagnosisMapper.toResponseDto(diagnostico);
    }

    private void validarReglasDiagnostico(DiagnosisRequestDto request, boolean tieneReparacion) {
        if (tieneReparacion) {
            if (request.getValorManoObra() == null
                    || request.getValorManoObra().compareTo(BigDecimal.ZERO) <= 0
                    || request.getTiempoEstimado() == null) {
                throw new IllegalArgumentException(
                        "Debe ingresar valores numéricos positivos para los costos estimados y especificar el tiempo de entrega."
                );
            }
        } else if (request.getJustificacionInvalida() == null
                || request.getJustificacionInvalida().trim().length() < 15) {
            throw new IllegalArgumentException(
                    "Debe ingresar una justificación técnica de al menos 15 caracteres cuando el equipo no tiene reparación."
            );
        }
    }
}
