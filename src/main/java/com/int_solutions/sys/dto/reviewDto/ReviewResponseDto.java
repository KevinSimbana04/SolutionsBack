package com.int_solutions.sys.dto.reviewDto;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long resenaId,
        Long ordenId,
        String codigoOrden,
        String tecnicoNombreCompleto,
        Integer calificacion,
        String comentario,
        LocalDateTime fechaCreacion
) {
}
