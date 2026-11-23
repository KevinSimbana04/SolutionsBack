package com.int_solutions.sys.controller;

import com.int_solutions.sys.dto.repairDto.RepairRequestDto;
import com.int_solutions.sys.dto.repairDto.RepairResponseDto;
import com.int_solutions.sys.service.repairService.RepairService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes/{ordenId}/reparacion")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;

    @PostMapping
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<RepairResponseDto> registrarReparacion(
            @PathVariable Long ordenId,
            @Valid @RequestBody RepairRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repairService.registrarAvanceReparacion(ordenId, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TECNICO', 'ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<List<RepairResponseDto>> listarBitacora(@PathVariable Long ordenId) {
        return ResponseEntity.ok(repairService.listarPorOrden(ordenId));
    }
}