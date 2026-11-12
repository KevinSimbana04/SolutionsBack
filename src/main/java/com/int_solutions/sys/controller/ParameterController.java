package com.int_solutions.sys.controller;

import com.int_solutions.sys.dto.parameterDto.ParameterGeneralDto;
import com.int_solutions.sys.service.parameterService.ParameterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parametros")
@RequiredArgsConstructor
public class ParameterController {
    private final ParameterService parameterService;

    @GetMapping
    @PreAuthorize("hasAuthority('PARAMETRO_LEER') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ParameterGeneralDto> obtenerParametros() {
        return ResponseEntity.ok(parameterService.obtenerParametros());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('PARAMERTRO_EDITAR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<ParameterGeneralDto> actualizarParametros(
            @Valid @RequestBody ParameterGeneralDto request,
            Authentication authentication) {
        return ResponseEntity.ok(parameterService.actualizarParametros(request, authentication.getName()));
    }
}
