package com.int_solutions.sys.dto.equipmentDto;

import lombok.Builder;

@Builder
public record EquipmentResponseDto(
        Long equipoId,
        Long clienteId,
        String nombreCliente,
        String cedulaCliente,
        String tipoEquipo,
        String marca,
        String modelo,
        String numeroSerie,
        String color,
        String accesorios,
        String estadoFisicoInicial,
        String fotografiaInicial,
        String fallaReportada,
        String observaciones,
        String mensaje
) {
}
