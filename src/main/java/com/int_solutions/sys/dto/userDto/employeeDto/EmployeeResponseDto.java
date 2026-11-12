package com.int_solutions.sys.dto.userDto.employeeDto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EmployeeResponseDto(
        Long empleadoId,
        Long usuarioId,
        String identificacion,
        String nombres,
        String apellidos,
        String telefono,
        String direccion,
        String cargo,
        String rol,
        String correo,
        Boolean estadoCuenta,
        Boolean bloqueado,
        LocalDate fechaCreacion,
        String mensaje
) {
}
