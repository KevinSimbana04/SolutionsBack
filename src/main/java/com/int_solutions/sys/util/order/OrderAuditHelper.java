package com.int_solutions.sys.util.order;

import com.int_solutions.sys.entity.auth.RoleE;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.entity.workOrder.Order;
import com.int_solutions.sys.entity.workOrder.OrderHistory;
import com.int_solutions.sys.entity.workOrder.OrderStatus;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import com.int_solutions.sys.repository.OrderHistoryRepository;
import com.int_solutions.sys.repository.OrderRepository;
import com.int_solutions.sys.repository.OrderStatusRepository;
import com.int_solutions.sys.util.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class OrderAuditHelper {
    public final OrderRepository orderRepository;
    public final OrderStatusRepository orderStatusRepository;
    public final OrderHistoryRepository orderHistoryRepository;
    public final SecurityContextHelper securityContextHelper;


    public String codeGenerator() {
        String prefijo = "OT-" + LocalDate.now().getYear() + "-";
        long correlativo = orderRepository.countByCodigoStartingWith(prefijo) + 1;
        return prefijo + String.format("%04d", correlativo);
    }

    public Order buscarOrden(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden de trabajo no encontrada."));
    }

    public OrderStatus obtenerEstado(OrderStatusE estadoEnum) {
        return orderStatusRepository.findByTipoEstado(estadoEnum)
                .orElseThrow(() -> new IllegalStateException("Error de configuración: El estadoOrden '" + estadoEnum + "' no existe en la base de datos."));
    }

    public void registrarHistorial(Order orden, OrderStatus anterior, OrderStatus nuevo, User usuario, String notas) {
        OrderHistory historial = OrderHistory.builder()
                .orden(orden)
                .estadoAnterior(anterior)
                .estadoNuevo(nuevo)
                .usuario(usuario)
                .notasSeguimiento(notas)
                .fechaModificacion(LocalDateTime.now())
                .build();

        orderHistoryRepository.save(historial);
    }

    public void validarTecnicoAsignado(Order orden, Employee tecnico) {
        if (orden.getTecnico() == null || !orden.getTecnico().getEmpleadoId().equals(tecnico.getEmpleadoId())) {
            throw new AccessDeniedException("No tiene autorización para intervenir una orden asignada a otro técnico.");
        }
    }

    public void validarLecturaOrden(Order orden) {
        User usuario = securityContextHelper.obtenerUsuarioEnSesion();

        boolean esGestion = usuario.getRoles().stream()
                .anyMatch(r -> r.getNombreRol() == RoleE.ADMINISTRADOR
                        || r.getNombreRol() == RoleE.ADMINISTRATIVO);

        if (!esGestion) {
            Employee tecnico = securityContextHelper.obtenerEmpleadoEnSesion();
            validarTecnicoAsignado(orden, tecnico);
        }
    }

}
