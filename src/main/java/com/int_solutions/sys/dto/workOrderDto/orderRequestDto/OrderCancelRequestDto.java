package com.int_solutions.sys.dto.workOrderDto.orderRequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelRequestDto {
    @NotBlank(message = "El motivo de cancelación es obligatorio")
    @Size(min = 10, message = "El motivo debe tener al menos 10 caracteres explicativos")
    private String motivo;

}
