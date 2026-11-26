package com.int_solutions.sys.dto.workOrderDto.orderRequestDto;

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
public class OrderDeliveryRequestDto {

    @NotBlank(message = "El nombre de la persona que recibe es obligatorio")
    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
    private String personaRecibe;

    @NotBlank(message = "La identificación de quien retira el equipo es obligatoria")
    @Size(min = 5, max = 20, message = "La identificación debe tener entre 5 y 20 caracteres")
    private String identificacionRecibe;

    @Size(max = 300, message = "Las observaciones de entrega no pueden superar 300 caracteres")
    private String observacionesEntrega;
}