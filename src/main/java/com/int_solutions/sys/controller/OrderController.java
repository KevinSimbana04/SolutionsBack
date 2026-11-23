package com.int_solutions.sys.controller;

import com.int_solutions.sys.dto.filterDto.OrderFilterDto;
import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderCancelRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderCreateRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderRequestDto.OrderUpdateReceptionRequestDto;
import com.int_solutions.sys.dto.workOrderDto.orderResponseDto.OrderClientDetailResponseDto;
import com.int_solutions.sys.dto.workOrderDto.orderResponseDto.OrderClientSummaryDto;
import com.int_solutions.sys.dto.workOrderDto.orderResponseDto.OrderDetailResponseDto;
import com.int_solutions.sys.dto.workOrderDto.orderResponseDto.OrderResponseDto;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import com.int_solutions.sys.service.workOrderService.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('ORDEN_CREAR') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<OrderResponseDto> crearOrden(@Valid @RequestBody OrderCreateRequestDto request) {
        OrderResponseDto response = orderService.crearOrden(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/recepcion")
    @PreAuthorize("hasAuthority('ORDEN_EDITAR_RECEPCION') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<OrderResponseDto> actualizarRecepcion(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateReceptionRequestDto request) {
        OrderResponseDto response = orderService.actualizarRecepcion(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('ORDEN_CANCELAR') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<OrderResponseDto> cancelarOrden(
            @PathVariable Long id,
            @Valid @RequestBody OrderCancelRequestDto request) {
        OrderResponseDto response = orderService.cancelarOrden(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ORDDEN_LEER_TODAS', 'ORDEN_LEER_ASIGNADOS') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR', 'TECNICO'    )")
    public ResponseEntity<OrderDetailResponseDto> obtenerOrdenDetalle(@PathVariable Long id) {
        OrderDetailResponseDto response = orderService.obtenerOrdenDetalle(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tecnico")
    @PreAuthorize("hasAuthority('ORDEN_LEER_ASIGNADOS') or hasRole('TECNICO')")
    public ResponseEntity<List<OrderResponseDto>> listarBandejaTecnico(
            @RequestParam(required = false) OrderStatusE estado,
            @RequestParam(required = false) String busqueda){

        OrderFilterDto filtro = OrderFilterDto.paraAdmin(null, estado, busqueda);
        return ResponseEntity.ok(orderService.listarOrdenesTectico(filtro));
    }

    @PatchMapping("/tecnico/{id}/confirmar-recepcion")
    @PreAuthorize("hasAuthority('ORDEN_CONFIRMAR_RECEPCION') or hasRole('TECNICO')")
    public ResponseEntity<OrderResponseDto> confirmarRecepcion(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.confirmarRecepcionFisica(id));
    }

    @PatchMapping("/tecnico/{id}/iniciar-diagnostico")
    @PreAuthorize("hasAuthority('ORDEN_INICIAR_DIAGNOSTICO') or hasRole('TECNICO')")
    public ResponseEntity<OrderResponseDto> iniciarDiagnostico(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.iniciarDiagnostico(id));
    }


    @GetMapping("/cliente/mis-reparaciones")
    @PreAuthorize("hasAuthority('ORDEN_LEER_PROPIAS') or hasRole('CLIENTE')")
    public ResponseEntity<List<OrderClientSummaryDto>> listarMisReparacionesCliente() {
        return ResponseEntity.ok(orderService.listarOrdenesCliente());
    }

    @GetMapping("/cliente/mis-reparaciones/{id}")
    @PreAuthorize("hasAuthority('ORDEN_LEER_PROPIAS') or hasRole('CLIENTE')")
    public ResponseEntity<OrderClientDetailResponseDto> detalleReparacionCliente(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.obtenerDetalleOrdenCliente(id));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ORDDEN_LEER_TODAS') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<Page<OrderResponseDto>> consultaGlobalAdmin(
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) OrderStatusE estado,
            @RequestParam(required = false) String busqueda,
            Pageable pageable) {

        OrderFilterDto filtro = new OrderFilterDto(tecnicoId, null, estado, busqueda);
        return ResponseEntity.ok(orderService.consultarGlobalAdmin(filtro, pageable));
    }

}
