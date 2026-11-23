package com.int_solutions.sys.dto.workOrderDto.orderRequestDto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateReceptionRequestDto {
    @Positive
     private Long tecnicoId;

    @Size(min = 10, message = "La falla reportada debe tener al menos 10 caracteres")
    private String fallaReportada;

    private String accesorios;

    private String motivoReasignacion;
}
