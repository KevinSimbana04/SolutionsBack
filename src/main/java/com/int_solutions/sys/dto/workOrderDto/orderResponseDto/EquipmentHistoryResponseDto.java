package com.int_solutions.sys.dto.workOrderDto.orderResponseDto;

import java.util.List;

public record EquipmentHistoryResponseDto(
        Long equipoId,
        String marca,
        String modelo,
        String numeroSerie,
        String tipoEquipo,
        List<EquipmentHistoryItemDto> reparacionesFinalizadas
) {
}