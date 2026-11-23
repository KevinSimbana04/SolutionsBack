package com.int_solutions.sys.service.repairService;

import com.int_solutions.sys.dto.repairDto.RepairRequestDto;
import com.int_solutions.sys.dto.repairDto.RepairResponseDto;
import com.int_solutions.sys.entity.repair.Repair;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.entity.workOrder.Order;
import com.int_solutions.sys.entity.workOrder.OrderStatus;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import com.int_solutions.sys.mapper.RepairMapper;
import com.int_solutions.sys.repository.BudgetRepository;
import com.int_solutions.sys.repository.EmployeeRepository;
import com.int_solutions.sys.repository.OrderRepository;
import com.int_solutions.sys.repository.RepairRepository;
import com.int_solutions.sys.util.SecurityContextHelper;
import com.int_solutions.sys.util.order.OrderAuditHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class RepairServiceImp implements RepairService{
    private final RepairRepository repairRepository;
    private final BudgetRepository budgetRepository;
    private final SecurityContextHelper securityContextHelper;
    private final OrderAuditHelper orderAuditHelper;
    private final RepairMapper repairMapper;

    private static final Set<OrderStatusE> ESTADOS_HABILITADOS = Set.of(
            OrderStatusE.PRESUPUESTO_APROBADO,
            OrderStatusE.EN_REPARACION,
            OrderStatusE.EN_ESPERA_REPUESTOS,
            OrderStatusE.EN_PRUEBAS,
            OrderStatusE.REQUIERE_NUEVA_INTERVENCION
    );

    @Override
    @Transactional
    public RepairResponseDto registrarAvanceReparacion(Long ordenId, RepairRequestDto request) {
        Employee tecnico = securityContextHelper.obtenerEmpleadoEnSesion();
        Order orden = orderAuditHelper.buscarOrden(ordenId);
        orderAuditHelper.validarTecnicoAsignado(orden, tecnico);

        if (!ESTADOS_HABILITADOS.contains(orden.getEstadoOrden().getTipoEstado())) {
            throw new IllegalStateException("La orden debe contar con el Presupuesto aprobado para gestionar la reparación.");
        }

        OrderStatusE nuevoEnum = resolverSiguienteEstado(request);
        LocalDateTime fechaFin = (nuevoEnum == OrderStatusE.LISTO_PARA_ENTREGA) ? LocalDateTime.now() : null;

        marcarRepuestosUtilizados(ordenId, request.getRepuestosUtilizadosIds());

        Repair guardada = repairRepository.save(
                repairMapper.toEntity(request, orden, tecnico, fechaFin)
        );

        OrderStatus estadoAnterior = orden.getEstadoOrden();
        OrderStatus nuevoEstado = orderAuditHelper.obtenerEstado(nuevoEnum);
        orden.setEstadoOrden(nuevoEstado);

        String detalleHistorial = request.getBitacoraDeAcciones()
                + (request.getResultadoPrueba() != null ? " | Resultado: " + request.getResultadoPrueba() : "")
                + (request.getMotivoFallo() != null ? " | Motivo: " + request.getMotivoFallo() : "");

        orderAuditHelper.registrarHistorial(orden, estadoAnterior, nuevoEstado, tecnico.getUsuario(), detalleHistorial);

        return repairMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairResponseDto> listarPorOrden(Long ordenId) {
        Order orden = orderAuditHelper.buscarOrden(ordenId);
        orderAuditHelper.validarLecturaOrden(orden);

        return repairMapper.toResponseDtoList(
                repairRepository.findByOrdenTrabajo_OrdenIdOrderByFechaInicioAsc(ordenId)
        );
    }

    private OrderStatusE resolverSiguienteEstado(RepairRequestDto request) {
        return switch (request.getEstadoOperativo()) {
            case "EN_REPARACION" -> OrderStatusE.EN_REPARACION;
            case "EN_ESPERA_REPUESTOS" -> {
                if (request.getMotivoFallo() == null || request.getMotivoFallo().trim().length() < 10) {
                    throw new IllegalArgumentException("Debe ingresar el motivo de pausa por espera de repuestos (mínimo 10 caracteres).");
                }
                yield OrderStatusE.EN_ESPERA_REPUESTOS;
            }
            case "REPARACION_TERMINADA" -> OrderStatusE.EN_PRUEBAS;
            case "EN_PRUEBAS" -> {
                if (request.getResultadoPrueba() == null) {
                    throw new IllegalArgumentException("Debe seleccionar el resultado de las pruebas de calidad.");
                }
                if ("PRUEBAS_SATISFACTORIAS".equals(request.getResultadoPrueba())) {
                    if (request.getHistorialPruebas() == null || request.getHistorialPruebas().trim().length() < 10) {
                        throw new IllegalArgumentException("Las observaciones de pruebas son obligatorias (mínimo 10 caracteres).");
                    }
                    yield OrderStatusE.LISTO_PARA_ENTREGA;
                }
                if (request.getMotivoFallo() == null || request.getMotivoFallo().trim().length() < 10) {
                    throw new IllegalArgumentException("Debe registrar el motivo del fallo en pruebas (mínimo 10 caracteres).");
                }
                yield OrderStatusE.EN_REPARACION;
            }
            default -> throw new IllegalArgumentException("Estado operativo no soportado.");
        };
    }

    private void marcarRepuestosUtilizados(Long ordenId, List<Long> repuestosIds) {
        if (repuestosIds == null || repuestosIds.isEmpty()) {
            return;
        }
        budgetRepository.findByOrdenTrabajo_OrdenId(ordenId).ifPresent(budget -> {
            budget.getDetallesRepuesto().stream()
                    .filter(item -> repuestosIds.contains(item.getId()))
                    .forEach(item -> item.setUtilizadoReparacion(true));
            budgetRepository.save(budget);
        });
    }
}
