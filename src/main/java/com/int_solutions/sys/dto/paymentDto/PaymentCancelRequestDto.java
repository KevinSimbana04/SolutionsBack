package com.int_solutions.sys.dto.paymentDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelRequestDto {

    @NotBlank(message = "El motivo de anulación es obligatorio")
    @Size(min = 10, message = "El motivo de anulación debe tener al menos 10 caracteres")
    private String motivoAnulacion;
}