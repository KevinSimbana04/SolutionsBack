package com.int_solutions.sys.service.workOrderService;

import com.int_solutions.sys.dto.filterDto.OrderFilterDto;
import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderCancelRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderCreateRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderDeliveryRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderUpdateReceptionRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderResponseDto.*;
import com.int_solutions.sys.entity.auth.RoleE;
import com.int_solutions.sys.entity.budget.Budget;
import com.int_solutions.sys.entity.budget.DetailSparePart;
import com.int_solutions.sys.entity.diagnosis.Diagnosis;
import com.int_solutions.sys.entity.equipment.Equipment;
import com.int_solutions.sys.entity.user.Customer;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.entity.workOrder.Order;
import com.int_solutions.sys.entity.workOrder.OrderStatus;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import com.int_solutions.sys.mapper.OrderMapper;
import com.int_solutions.sys.repository.*;
import com.int_solutions.sys.repository.specification.OrderSpecification;
import com.int_solutions.sys.util.SecurityContextHelper;
import com.int_solutions.sys.util.order.OrderAuditHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImp  implements  OrderService{

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final EquipmentRepository equipmentRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final BudgetRepository budgetRepository;
    private final PaymentRepository paymentRepository;
    private final ParameterRepository parameterRepository;
    private final DiagnosisRepository diagnosisRepository;

    private final OrderAuditHelper settingOrder;
    private final SecurityContextHelper securityContextHelper;
    private final OrderMapper orderMapper;

    private static final Set<OrderStatusE> ESTADOS_INICIALES = Set.of(
            OrderStatusE.SOLICITUD_RECIBIDA,
            OrderStatusE.EQUIPO_INGRESADO,
            OrderStatusE.PENDIENTE_ASIGNACION,
            OrderStatusE.ASIGNADO_A_TECNICO
    );

    private static final Set<OrderStatusE> ESTADOS_FINALIZADOS = Set.of(
            OrderStatusE.ENTREGADO,
            OrderStatusE.CANCELADO,
            OrderStatusE.SIN_REPARACION
    );

    private static final Set<OrderStatusE> ESTADOS_ENTREGABLES = Set.of(
            OrderStatusE.LISTO_PARA_ENTREGA,
            OrderStatusE.SIN_REPARACION,
            OrderStatusE.PRESUPUESTO_RECHAZADO
    );

    @Override
    @Transactional
    public OrderResponseDto crearOrden(OrderCreateRequestDto request) {

        Employee administrativo = securityContextHelper.obtenerEmpleadoEnSesion();

        Customer cliente = customerRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no existe."));

        Equipment equipo = equipmentRepository.findById(request.getEquipoId())
                .orElseThrow(() -> new IllegalArgumentException("Equipo no existe."));

        if (orderRepository.existsByEquipo_EquipoIdAndEstadoOrden_TipoEstadoNotIn(
                equipo.getEquipoId(), ESTADOS_FINALIZADOS)) {
            throw new IllegalStateException("El equipo ya tiene una orden de trabajo activa sin finalizar.");
        }

        if (!equipo.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new IllegalArgumentException("El equipo no pertenece al cliente indicado.");
        }

        Employee tecnico = validarYObtenerTecnico(request.getTecnicoId());

        OrderStatus estadoInicial = settingOrder.obtenerEstado(OrderStatusE.ASIGNADO_A_TECNICO);

        Order nuevaOrden = orderMapper.toEntity(
                request,
                settingOrder.codeGenerator(),
                estadoInicial,
                administrativo,
                tecnico,
                cliente,
                equipo
        );

        Order guardada = orderRepository.save(nuevaOrden);

        settingOrder.registrarHistorial(guardada, null, estadoInicial, administrativo.getUsuario(),
                "Creación de orden y asignación a " + tecnico.getNombres() + " " + tecnico.getApellidos());

        return orderMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional
    public OrderResponseDto actualizarRecepcion(Long id, OrderUpdateReceptionRequestDto request) {
        Order orden = settingOrder.buscarOrden(id);
        validarEdicionPermitida(orden);

        boolean reasignacion = request.getTecnicoId() != null
                && !orden.getTecnico().getEmpleadoId().equals(request.getTecnicoId());

        if (reasignacion) {
            if (request.getMotivoReasignacion() == null || request.getMotivoReasignacion().trim().length() < 10) {
                throw new IllegalArgumentException("Debe justificar la reasignación (mínimo 10 caracteres).");
            }
            Employee nuevoTecnico = validarYObtenerTecnico(request.getTecnicoId());

            orden.setTecnico(nuevoTecnico);
            settingOrder.registrarHistorial(orden, orden.getEstadoOrden(), orden.getEstadoOrden(), securityContextHelper.obtenerUsuarioEnSesion(),
                    "Reasignación técnica. Motivo: " + request.getMotivoReasignacion());
        }

        if (request.getFallaReportada() != null) {
            orden.getEquipo().setFallaReportada(request.getFallaReportada());
        }
        if (request.getAccesorios() != null) {
            orden.getEquipo().setAccesorios(request.getAccesorios());
        }

        return orderMapper.toResponseDto(orderRepository.save(orden));
    }

    @Override
    @Transactional
    public OrderResponseDto cancelarOrden(Long id, OrderCancelRequestDto request) {
        Order orden = settingOrder.buscarOrden(id);

        if (request.getMotivo() == null || request.getMotivo().trim().length() < 10) {
            throw new IllegalArgumentException("El motivo de cancelación es obligatorio y debe tener al menos 10 caracteres.");
        }

        OrderStatus estadoCancelado = settingOrder.obtenerEstado(OrderStatusE.CANCELADO);
        OrderStatus estadoAnterior = orden.getEstadoOrden();

        orden.setEstadoOrden(estadoCancelado);
        orden.setMotivo(request.getMotivo());

        settingOrder.registrarHistorial(orden, estadoAnterior, estadoCancelado, securityContextHelper.obtenerUsuarioEnSesion(),
                "Cancelación. Motivo: " + request.getMotivo());

        return orderMapper.toResponseDto(orderRepository.save(orden));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDto obtenerOrdenDetalle(Long ordenId) {
        Order orden = settingOrder.buscarOrden(ordenId);
        settingOrder.validarLecturaOrden(orden);

        return orderMapper.toDetailResponseDto(
                orden,
                orderHistoryRepository.findByOrdenOrdenIdOrderByFechaModificacionAsc(ordenId)
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> listarOrdenesTectico(OrderFilterDto orderFilterDto){

        Employee tecnico = securityContextHelper.obtenerEmpleadoEnSesion();
        OrderFilterDto filterTecnico = OrderFilterDto.paraTecnico(tecnico.getEmpleadoId(), orderFilterDto);
        Sort ordenCronologico = Sort.by(Sort.Order.asc("fechaCreacion"), Sort.Order.asc("ordenId"));

        return orderMapper.toResponseDtoList(
                orderRepository.findAll(OrderSpecification.conFiltros(filterTecnico), ordenCronologico)
        );
    }

    @Override
    @Transactional
    public OrderResponseDto confirmarRecepcionFisica(Long ordenId) {
        return cambiarEstadoTecnico(
                ordenId,
                OrderStatusE.ASIGNADO_A_TECNICO,
                OrderStatusE.RECIBIDO_POR_TECNICO,
                "Técnico confirmó la recepción física del equipo en mesa de trabajo."
        );
    }

    @Override
    @Transactional
    public OrderResponseDto iniciarDiagnostico(Long ordenId) {
        return cambiarEstadoTecnico(
                ordenId,
                OrderStatusE.RECIBIDO_POR_TECNICO,
                OrderStatusE.EN_DIAGNOSTICO,
                "Técnico inició el diagnóstico del equipo."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderClientSummaryDto> listarOrdenesCliente() {
        Customer cliente = securityContextHelper.obtenerClienteEnSesion();
        OrderFilterDto filtroCliente = OrderFilterDto.paraCliente(cliente.getClienteId());
        Sort ordenReciente = Sort.by(Sort.Order.desc("fechaCreacion"), Sort.Order.desc("ordenId"));

        return orderMapper.toClientSummaryDtoList(
                orderRepository.findAll(OrderSpecification.conFiltros(filtroCliente), ordenReciente)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderClientDetailResponseDto obtenerDetalleOrdenCliente(Long ordenId) {
        Customer cliente = securityContextHelper.obtenerClienteEnSesion();
        Order orden = settingOrder.buscarOrden(ordenId);

        if (!orden.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new AccessDeniedException("No tiene autorización para visualizar la información de esta orden.");
        }

        return orderMapper.toClientDetailResponseDto(
                orden,
                orderHistoryRepository.findByOrdenOrdenIdOrderByFechaModificacionAsc(ordenId)
        );
    }


    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> consultarGlobalAdmin(OrderFilterDto filtro, Pageable pageable) {
        return orderRepository.findAll(OrderSpecification.conFiltros(filtro), pageable)
                .map(orderMapper::toResponseDto);
    }

    @Override
    @Transactional
    public OrderResponseDto entregarEquipo(Long ordenId, OrderDeliveryRequestDto request) {
        User usuario = securityContextHelper.obtenerUsuarioEnSesion();
        Order orden = settingOrder.buscarOrden(ordenId);
        OrderStatusE estadoActual = orden.getEstadoOrden().getTipoEstado();

        if (!ESTADOS_ENTREGABLES.contains(estadoActual)) {
            throw new IllegalStateException("La orden debe estar lista para entrega o cerrada para retiro antes de entregar el equipo.");
        }
        if (estadoActual == OrderStatusE.LISTO_PARA_ENTREGA) {
            validarSaldoCeroParaEntrega(ordenId);
        }

        OrderStatus estadoAnterior = orden.getEstadoOrden();
        OrderStatus estadoEntregado = settingOrder.obtenerEstado(OrderStatusE.ENTREGADO);

        orden.setEstadoOrden(estadoEntregado);
        orden.setFechaEntrega(LocalDate.now());
        orden.setPersonaRecibe(request.getPersonaRecibe().trim());
        orden.setIdentificacionRecibe(request.getIdentificacionRecibe().trim());
        orden.setObservacionesEntrega(request.getObservacionesEntrega());

        Order guardada = orderRepository.save(orden);
        settingOrder.registrarHistorial(
                guardada, estadoAnterior, estadoEntregado, usuario,
                "Equipo entregado a " + orden.getPersonaRecibe() + " (CI: " + orden.getIdentificacionRecibe() + ")"
        );
        return orderMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentHistoryResponseDto consultarHistorialEquipoCliente(Long equipoId) {
        Customer cliente = securityContextHelper.obtenerClienteEnSesion();
        Equipment equipo = equipmentRepository.findById(equipoId)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado."));

        if (!equipo.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new AccessDeniedException("No tiene autorización para consultar el historial de este equipo.");
        }

        int diasGarantia = parameterRepository.findByClave("TIEMPO_GARANTIA")
                .map(p -> Integer.parseInt(p.getValor()))
                .orElse(2);

        List<EquipmentHistoryItemDto> items = orderRepository
                .findByEquipo_EquipoIdAndEstadoOrden_TipoEstadoInOrderByFechaEntregaDescFechaCreacionDesc(
                        equipoId, List.of(OrderStatusE.ENTREGADO, OrderStatusE.SIN_REPARACION))
                .stream()
                .map(orden -> mapearItemHistorial(orden, diasGarantia))
                .toList();

        return orderMapper.toEquipmentHistoryResponseDto(equipo, items);
    }

    private OrderResponseDto cambiarEstadoTecnico(
            Long ordenId, OrderStatusE estadoEsperado, OrderStatusE nuevoEstadoEnum, String observacion) {

        Employee tecnico = securityContextHelper.obtenerEmpleadoEnSesion();
        Order orden = settingOrder.buscarOrden(ordenId);
        settingOrder.validarTecnicoAsignado(orden, tecnico);

        if (orden.getEstadoOrden().getTipoEstado() != estadoEsperado) {
            throw new IllegalStateException("La orden debe estar en estadoOrden " + estadoEsperado + " para realizar esta acción.");
        }

        OrderStatus estadoAnterior = orden.getEstadoOrden();
        OrderStatus estadoNuevo = settingOrder.obtenerEstado(nuevoEstadoEnum);

        orden.setEstadoOrden(estadoNuevo);
        Order guardada = orderRepository.save(orden);
        settingOrder.registrarHistorial(guardada, estadoAnterior, estadoNuevo, tecnico.getUsuario(), observacion);

        return orderMapper.toResponseDto(guardada);
    }

    private void validarEdicionPermitida(Order orden) {
        if (!ESTADOS_INICIALES.contains(orden.getEstadoOrden().getTipoEstado())) {
            throw new IllegalArgumentException("La orden se encuentra en etapa técnica activa. Solo se admiten transiciones de estadoOrden y notas de seguimiento.");
        }
    }

    private Employee validarYObtenerTecnico(Long tecnicoId) {
        Employee tecnico = employeeRepository.findById(tecnicoId)
                .orElseThrow(() -> new IllegalArgumentException("El técnico asignado no existe."));

        boolean esTecnico = tecnico.getUsuario().getRoles().stream()
                .anyMatch(r -> r.getNombreRol() == RoleE.TECNICO);

        if (!esTecnico) {
            throw new IllegalArgumentException("El empleado seleccionado no tiene el rol de TÉCNICO.");
        }

        if (Boolean.FALSE.equals(tecnico.getUsuario().getEstadoCuenta())) {
            throw new IllegalStateException("El técnico seleccionado se encuentra inactivo.");
        }

        return tecnico;
    }

    private LocalDate sumarDiasHabiles(LocalDate inicio, int diasHabiles) {
        LocalDate fecha = inicio;
        int sumados = 0;
        while (sumados < diasHabiles) {
            fecha = fecha.plusDays(1);
            var dia = fecha.getDayOfWeek();
            if (dia != java.time.DayOfWeek.SATURDAY && dia != java.time.DayOfWeek.SUNDAY) {
                sumados++;
            }
        }
        return fecha;
    }

    private void validarSaldoCeroParaEntrega(Long ordenId) {
        Budget presupuesto = budgetRepository.findByOrdenTrabajo_OrdenId(ordenId)
                .orElseThrow(() -> new IllegalStateException("No existe presupuesto asociado a esta orden."));
        BigDecimal abonado = paymentRepository.sumarTotalAbonadoActivo(ordenId);
        if (presupuesto.getTotalPagar().subtract(abonado).compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException(
                    "El equipo registra un saldo pendiente. El sistema no podrá registrar la entrega y denegará la autorización de salida del equipo."
            );
        }
    }

    private EquipmentHistoryItemDto mapearItemHistorial(Order orden, int diasGarantia) {
        String diagResumen = diagnosisRepository.findByOrdenTrabajo_OrdenId(orden.getOrdenId())
                .map(Diagnosis::getCausaProbableTecnico)
                .orElse("Sin diagnóstico registrado");

        List<String> repuestos = budgetRepository.findByOrdenTrabajo_OrdenId(orden.getOrdenId())
                .map(b -> b.getDetallesRepuesto().stream()
                        .filter(d -> Boolean.TRUE.equals(d.getUtilizadoReparacion()))
                        .map(DetailSparePart::getNombreRepuesto)
                        .toList())
                .orElse(List.of());

        LocalDate fechaFinGarantia = (orden.getFechaEntrega() != null
                && orden.getEstadoOrden().getTipoEstado() == OrderStatusE.ENTREGADO)
                ? sumarDiasHabiles(orden.getFechaEntrega(), diasGarantia)
                : null;

        return orderMapper.toEquipmentHistoryItemDto(orden, diagResumen, repuestos, diasGarantia, fechaFinGarantia);
    }
}