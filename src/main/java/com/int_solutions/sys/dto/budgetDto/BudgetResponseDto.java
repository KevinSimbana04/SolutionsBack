package com.int_solutions.sys.dto.budgetDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BudgetResponseDto(
        Long id,
        Long ordenId,
        String codigoOrden,
        String equipoMarcaModelo,
        BigDecimal costoManoObra,
        BigDecimal costoRepuestos,
        BigDecimal subtotal,
        BigDecimal valorIva,
        BigDecimal totalPagar,
        String estadoPresupuesto,
        Boolean confirmacion,
        String motivoRechazo,
        LocalDate fechaEstimadaEntrega,
        LocalDateTime fechaEnvio,
        LocalDateTime fechaLimite,
        List<SparePartResponseDto> detallesRepuesto
) {
    public record SparePartResponseDto(
            Long id,
            String nombreRepuesto,
            Integer cantidad,
            BigDecimal costoUnitario,
            Boolean utilizadoReparacion
    ) {}
}
