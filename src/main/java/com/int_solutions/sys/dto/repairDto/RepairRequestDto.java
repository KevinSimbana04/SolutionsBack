package com.int_solutions.sys.dto.repairDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairRequestDto {
    @NotBlank(message = "El estadoOrden operativo es obligatorio")
    @Pattern(
            regexp = "^(EN_REPARACION|EN_ESPERA_REPUESTOS|REPARACION_TERMINADA|EN_PRUEBAS)$",
            message = "Estado operativo inválido"
    )
    private String estadoOperativo;

    @NotBlank(message = "La bitácora de acciones realizadas es obligatoria")
    @Size(min = 10, message = "La bitácora de acciones debe tener al menos 10 caracteres")
    private String bitacoraDeAcciones;

    // Obligatorio si estadoOperativo == EN_ESPERA_REPUESTOS o resultadoPrueba == PRUEBAS_FALLIDAS
    private String motivoFallo;

    // Obligatorio en la fase de pruebas (EN_PRUEBAS)
    @Pattern(regexp = "^(PRUEBAS_SATISFACTORIAS|PRUEBAS_FALLIDAS)$", message = "Resultado de pruebas inválido")
    private String resultadoPrueba;

    // Observaciones obligatorias al cerrar las pruebas
    private String historialPruebas;

    private String evidencia;

    // IDs de DetailSparePart utilizados en la reparación (opcional)
    private List<Long> repuestosUtilizadosIds;
}
