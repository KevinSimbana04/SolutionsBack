package com.int_solutions.sys.dto.paymentDto;

import com.int_solutions.sys.entity.payment.PaymentMethodE;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequestDto {

    @NotNull(message = "El monto a pagar es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a $0.00")
    private BigDecimal monto;

    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethodE metodoPago;

    @Size(min = 5, max = 30, message = "La referencia bancaria debe tener entre 5 y 30 caracteres")
    private String referenciaBancaria;

    private String comprobanteUrl;

    @Size(max = 300, message = "Las observaciones no pueden superar 300 caracteres")
    private String observaciones;
}