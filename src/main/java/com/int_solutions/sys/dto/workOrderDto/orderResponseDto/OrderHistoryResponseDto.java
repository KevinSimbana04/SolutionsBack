package com.int_solutions.sys.dto.workOrderDto.orderResponseDto;

import java.time.LocalDateTime;

public record OrderHistoryResponseDto (
        LocalDateTime fechaModificacion,
        String usuarioResponsable,
        String rolUsuario,
        String estadoAnterior,
        String estadoNuevo,
        String notasSeguimiento
){
}
