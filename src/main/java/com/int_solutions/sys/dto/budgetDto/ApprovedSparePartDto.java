package com.int_solutions.sys.dto.budgetDto;

public record ApprovedSparePartDto(
        Long id,
        String nombreRepuesto,
        Integer cantidad,
        Boolean utilizadoReparacion
) {
}