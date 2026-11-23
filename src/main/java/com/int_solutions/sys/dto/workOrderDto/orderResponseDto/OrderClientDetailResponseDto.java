package com.int_solutions.sys.dto.workOrderDto.orderResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrderClientDetailResponseDto (
        Long id,
        String codigo,
        String estadoActual,
        LocalDate fechaCreacion,
        LocalDate fechaEstimadaEntrega,
        String equipoMarcaModelo,
        String numeroSerie,
        String fotografiaInicial,
        List<EstadoLineaTiempoDto> lineaTiempo
){
    public record EstadoLineaTiempoDto(
            String estado,
            LocalDateTime fechaHora

    ) {}
}
