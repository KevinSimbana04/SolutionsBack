package com.int_solutions.sys.dto.authDto;

import lombok.Builder;

@Builder
public record AuthResponseDto(
        String token,
        String nombreUsuario,
        String rol,
        int tiempoExpiracionMinutos,
        boolean requiereCambioClave,
        String mensaje
)  {

}
