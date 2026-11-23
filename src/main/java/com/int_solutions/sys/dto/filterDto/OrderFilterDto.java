package com.int_solutions.sys.dto.filterDto;

import com.int_solutions.sys.entity.workOrder.OrderStatusE;

public record OrderFilterDto(
        Long tecnicoId,
        Long clienteId,
        OrderStatusE estado,
        String busqueda
) {
    public static OrderFilterDto paraTecnico(Long tecnicoId, OrderFilterDto base) {
        return new OrderFilterDto(tecnicoId, null, base.estado(), base.busqueda());
    }

    public static OrderFilterDto paraCliente(Long clienteId) {
        return new OrderFilterDto(null, clienteId, null, null);
    }

    public static OrderFilterDto paraAdmin(Long tecnicoId, OrderStatusE estado, String busqueda) {
        return new OrderFilterDto(tecnicoId, null, estado, busqueda);
    }
}
