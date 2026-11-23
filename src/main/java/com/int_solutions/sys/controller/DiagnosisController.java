package com.int_solutions.sys.controller;

import com.int_solutions.sys.dto.diagnosisDto.DiagnosisRequestDto;
import com.int_solutions.sys.dto.diagnosisDto.DiagnosisResponseDto;
import com.int_solutions.sys.service.diagnosisService.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordenes/{ordenId}/diagnostico")
@RequiredArgsConstructor
public class DiagnosisController {
    private final DiagnosisService diagnosisService;

    @PostMapping
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<DiagnosisResponseDto> emitirDiagnostico(
            @PathVariable Long ordenId,
            @Valid @RequestBody DiagnosisRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diagnosisService.emitirDiagnostico(ordenId, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TECNICO', 'ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<DiagnosisResponseDto> obtenerDiagnostico(@PathVariable Long ordenId) {
        return ResponseEntity.ok(diagnosisService.obtenerPorOrden(ordenId));
    }
}
