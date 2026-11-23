package com.int_solutions.sys.dto.diagnosisDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DiagnosisResponseDto(
        Long id,
        Long ordenId,
        String codigoOrden,
        String tecnicoNombreCompleto,
        String causaProbableTecnico,
        String pruebasRealizadas,
        String posibilidadesReparacion,
        String repuestosRequeridos,
        BigDecimal valorManoObra,
        Integer tiempoEstimado,
        String justificacionInvalida,
        String evidenciaFotografica,
        String observacionesTecnico,
        LocalDateTime fechaEmision,
        String nuevoEstadoOrden
) {
}