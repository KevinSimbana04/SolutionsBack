package com.int_solutions.sys.dto.userDto.customerDto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CustomerResponseDto(
        Long clienteId,
        Long usuarioId,
        String identificacion,
        String nombres,
        String apellidos,
        String telefono,
        String direccion,
        String correo,
        String nombreUsuario,
        String rol,
        Boolean estadoCuenta,
        String motivo,
        Boolean bloqueado,
        LocalDate fechaCreacion,
        String mensaje
) {
}
