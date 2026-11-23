package com.int_solutions.sys.dto.repairDto;

import java.time.LocalDateTime;

public record RepairResponseDto(
        Long id,
        Long ordenId,
        String codigoOrden,
        String tecnicoNombreCompleto,
        String estadoOperativo,
        String bitacoraDeAcciones,
        String resultadoPrueba,
        String motivoFallo,
        String historialPruebas,
        String evidencia,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        String nuevoEstadoOrden
) {
}