package com.int_solutions.sys.dto.reviewDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequestDto {

    @NotNull(message = "Debe seleccionar una calificación de 1 a 5 estrellas antes de enviar su reseña.")
    @Min(value = 1, message = "Debe seleccionar una calificación de 1 a 5 estrellas antes de enviar su reseña.")
    @Max(value = 5, message = "Debe seleccionar una calificación de 1 a 5 estrellas antes de enviar su reseña.")
    private Integer calificacion;

    @Size(max = 500, message = "El comentario no puede superar 500 caracteres")
    private String comentario;
}
