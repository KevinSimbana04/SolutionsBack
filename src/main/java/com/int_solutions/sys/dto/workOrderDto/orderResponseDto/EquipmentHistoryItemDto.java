package com.int_solutions.sys.dto.workOrderDto.orderResponseDto;

import java.time.LocalDate;
import java.util.List;

public record EquipmentHistoryItemDto(
        Long ordenId,
        String codigoOrden,
        String estadoOrden,
        LocalDate fechaRecepcion,
        LocalDate fechaEntrega,
        String fallaReportada,
        String diagnosticoAplicado,
        List<String> repuestosSustituidos,
        Integer diasGarantia,
        LocalDate fechaFinGarantia,
        boolean garantiaActiva
) {
}