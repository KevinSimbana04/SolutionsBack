package com.int_solutions.sys.mapper;

import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderCreateRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderResponseDto.*;
import com.int_solutions.sys.entity.equipment.Equipment;
import com.int_solutions.sys.entity.user.Customer;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.entity.workOrder.Order;
import com.int_solutions.sys.entity.workOrder.OrderHistory;
import com.int_solutions.sys.entity.workOrder.OrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    //Datos de Entrada
    @Mapping(target = "ordenId", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaEntrega", ignore = true)
    @Mapping(target = "personaRecibe", ignore = true)
    @Mapping(target = "motivo", ignore = true)
    @Mapping(source = "codigo", target = "codigo")
    @Mapping(source = "request.fechaEstimadaEntrega", target = "fechaEstimadaEntrega")
    @Mapping(source = "estadoInicial", target = "estadoOrden")
    @Mapping(source = "administrativo", target = "administrativo")
    @Mapping(source = "tecnico", target = "tecnico")
    @Mapping(source = "cliente", target = "cliente")
    @Mapping(source = "equipo", target = "equipo")
    @Mapping(target = "identificacionRecibe", ignore = true)
    @Mapping(target = "observacionesEntrega", ignore = true)
    Order toEntity(
            OrderCreateRequestDto request,
            String codigo,
            OrderStatus estadoInicial,
            Employee administrativo,
            Employee tecnico,
            Customer cliente,
            Equipment equipo
    );


    //Datos de Salida
    @Mapping(source = "ordenId", target = "ordenId")
    @Mapping(source = "estadoOrden.tipoEstado", target = "estadoOrden")
    @Mapping(source = "fechaEstimadaEntrega", target = "fechaEstimadaEntrega")
    @Mapping(source = "fechaEntrega", target = "fechaDeEntrega")
    @Mapping(source = "cliente.clienteId", target = "clienteId")
    @Mapping(target = "clienteNombreCompleto",
            expression = "java(orden.getCliente() != null ? orden.getCliente().getNombres() + \" \" + orden.getCliente().getApellidos() : null)")
    @Mapping(source = "equipo.equipoId", target = "equipoId")
    @Mapping(target = "equipoResumen",
            expression = "java(orden.getEquipo() != null ? orden.getEquipo().getMarca() + \" \" + orden.getEquipo().getModelo() : null)")
    @Mapping(source = "equipo.fallaReportada", target = "fallaReportada")
    @Mapping(source = "tecnico.empleadoId", target = "tecnicoId")
    @Mapping(target = "tecnicoNombreCompleto",
            expression = "java(orden.getTecnico() != null ? orden.getTecnico().getNombres() + \" \" + orden.getTecnico().getApellidos() : null)")
    OrderResponseDto toResponseDto(Order orden);
    List<OrderResponseDto> toResponseDtoList(List<Order> ordenes);


    @Mapping(target = "usuarioResponsable",
            expression = "java(historial.getUsuario() != null ? historial.getUsuario().getNombreUsuario() : null)")
    @Mapping(target = "rolUsuario",
            expression = "java(obtenerRolPrincipal(historial.getUsuario()))")
    @Mapping(source = "estadoAnterior.tipoEstado", target = "estadoAnterior")
    @Mapping(source = "estadoNuevo.tipoEstado", target = "estadoNuevo")
    OrderHistoryResponseDto toHistoryResponseDto(OrderHistory historial);

    default OrderDetailResponseDto toDetailResponseDto(Order orden, List<OrderHistory> historiales) {
        if (orden == null) {
            return null;
        }

        OrderResponseDto datosGenerales = toResponseDto(orden);

        List<OrderHistoryResponseDto> historialDtos = historiales == null ? null
                : historiales.stream().map(this::toHistoryResponseDto).toList();

        String adminNombre = orden.getAdministrativo() != null
                ? orden.getAdministrativo().getNombres() + " " + orden.getAdministrativo().getApellidos()
                : null;

        String accesorios = orden.getEquipo() != null ? orden.getEquipo().getAccesorios() : null;

        return new OrderDetailResponseDto(
                datosGenerales,
                orden.getFechaCreacion() != null ? orden.getFechaCreacion().atStartOfDay() : null,
                adminNombre,
                accesorios,
                orden.getMotivo(),
                historialDtos
        );
    }

    @Mapping(source = "ordenId", target = "ordenId")
    @Mapping(source = "estadoOrden.tipoEstado", target = "estadoOrden")
    @Mapping(source = "fechaEntrega", target = "fechaDeEntrega")
    @Mapping(target = "equipoResumen",
            expression = "java(orden.getEquipo() != null ? orden.getEquipo().getMarca() + \" \" + orden.getEquipo().getModelo() : null)")
    OrderClientSummaryDto toClientSummaryDto(Order orden);

    List<OrderClientSummaryDto> toClientSummaryDtoList(List<Order> ordenes);

    default OrderClientDetailResponseDto toClientDetailResponseDto(Order orden, List<OrderHistory> historiales) {
        if (orden == null) {
            return null;
        }

        var lineaTiempo = historiales == null ? List.<OrderClientDetailResponseDto.EstadoLineaTiempoDto>of()
                : historiales.stream()
                .map(h -> new OrderClientDetailResponseDto.EstadoLineaTiempoDto(
                        h.getEstadoNuevo().getTipoEstado().name(),
                        h.getFechaModificacion()
                ))
                .toList();

        return new OrderClientDetailResponseDto(
                orden.getOrdenId(),
                orden.getCodigo(),
                orden.getEstadoOrden().getTipoEstado().name(),
                orden.getFechaCreacion(),
                orden.getFechaEstimadaEntrega(),
                orden.getEquipo() != null ? orden.getEquipo().getMarca() + " " + orden.getEquipo().getModelo() : null,
                orden.getEquipo() != null ? orden.getEquipo().getNumeroSerie() : null,
                orden.getEquipo() != null ? orden.getEquipo().getFotografiaInicial() : null,
                lineaTiempo
        );
    }

    default String obtenerRolPrincipal(User usuario) {
        if (usuario != null && usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
            return usuario.getRoles().iterator().next().getNombreRol().name();
        }
        return "SIN_ROL";
    }

    @Mapping(source = "orden.ordenId", target = "ordenId")
    @Mapping(source = "orden.codigo", target = "codigoOrden")
    @Mapping(source = "orden.estadoOrden.tipoEstado", target = "estadoOrden")
    @Mapping(source = "orden.fechaCreacion", target = "fechaRecepcion")
    @Mapping(source = "orden.fechaEntrega", target = "fechaEntrega")
    @Mapping(source = "orden.equipo.fallaReportada", target = "fallaReportada")
    @Mapping(source = "diagnosticoAplicado", target = "diagnosticoAplicado")
    @Mapping(source = "repuestosSustituidos", target = "repuestosSustituidos")
    @Mapping(source = "diasGarantia", target = "diasGarantia")
    @Mapping(source = "fechaFinGarantia", target = "fechaFinGarantia")
    @Mapping(target = "garantiaActiva",
            expression = "java(fechaFinGarantia != null && !java.time.LocalDate.now().isAfter(fechaFinGarantia))")
    EquipmentHistoryItemDto toEquipmentHistoryItemDto(
            Order orden,
            String diagnosticoAplicado,
            List<String> repuestosSustituidos,
            Integer diasGarantia,
            java.time.LocalDate fechaFinGarantia
    );

    @Mapping(source = "equipo.equipoId", target = "equipoId")
    @Mapping(source = "equipo.marca", target = "marca")
    @Mapping(source = "equipo.modelo", target = "modelo")
    @Mapping(source = "equipo.numeroSerie", target = "numeroSerie")
    @Mapping(source = "equipo.tipoEquipo.nombreEquipo", target = "tipoEquipo")
    @Mapping(source = "items", target = "reparacionesFinalizadas")
    EquipmentHistoryResponseDto toEquipmentHistoryResponseDto(Equipment equipo, List<EquipmentHistoryItemDto> items);
}
