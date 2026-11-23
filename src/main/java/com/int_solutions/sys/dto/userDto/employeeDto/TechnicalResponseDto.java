package com.int_solutions.sys.dto.userDto.employeeDto;

public record TechnicalResponseDto(
        Long empleadoId,
        String nombres,
        String apellidos,
        String cargo
) {}