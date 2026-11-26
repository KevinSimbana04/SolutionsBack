package com.int_solutions.sys.service.workOrderService;

import com.int_solutions.sys.dto.filterDto.OrderFilterDto;
import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderCancelRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderCreateRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderDeliveryRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderUpdateReceptionRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderResponseDto.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface OrderService {
    OrderResponseDto crearOrden(OrderCreateRequestDto request);
    OrderResponseDto actualizarRecepcion(Long id, OrderUpdateReceptionRequestDto request);
    OrderResponseDto cancelarOrden(Long id, OrderCancelRequestDto request);
    OrderDetailResponseDto obtenerOrdenDetalle(Long id);

    //Ordenes para tecnico
    List<OrderResponseDto> listarOrdenesTectico(OrderFilterDto orderFilterDto);
    OrderResponseDto confirmarRecepcionFisica(Long ordenId);
    OrderResponseDto iniciarDiagnostico(Long orderId);

    //Ordenes para cliente
    List<OrderClientSummaryDto> listarOrdenesCliente();
    OrderClientDetailResponseDto obtenerDetalleOrdenCliente(Long ordenId);

    //Ordenes para Admin
    Page<OrderResponseDto> consultarGlobalAdmin(OrderFilterDto filtro, Pageable pageable);

    OrderResponseDto entregarEquipo(Long ordenId, OrderDeliveryRequestDto request);
    EquipmentHistoryResponseDto consultarHistorialEquipoCliente(Long equipoId);

}
