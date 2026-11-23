package com.int_solutions.sys.dto.budgetDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRespondRequestDto {
    @NotBlank(message = "La decisión es obligatoria (APROBAR o RECHAZAR)")
    @Pattern(regexp = "^(APROBAR|RECHAZAR)$", message = "La decisión debe ser APROBAR o RECHAZAR")
    private String decision;

    // Obligatorio en true cuando decision == APROBAR
    private Boolean confirmacion;

    // Obligatorio cuando decision == RECHAZAR
    private String motivoRechazo;

    // Obligatorio (5 a 200 caracteres) si motivoRechazo == OTRO
    @Size(min = 5, max = 200, message = "El comentario adicional debe tener entre 5 y 200 caracteres")
    private String comentarioAdicional;
}
