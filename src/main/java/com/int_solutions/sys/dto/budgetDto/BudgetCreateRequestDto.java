package com.int_solutions.sys.dto.budgetDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetCreateRequestDto {
    @NotNull(message = "El costo de mano de obra es obligatorio")
    @DecimalMin(value = "0.00", message = "El costo de mano de obra no puede ser negativo")
    private BigDecimal costoManoObra;

    @Valid
    private List<SparePartItemDto> repuestos;
}
