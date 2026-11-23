package com.int_solutions.sys.controller;

import com.int_solutions.sys.dto.budgetDto.ApprovedSparePartDto;
import com.int_solutions.sys.dto.budgetDto.BudgetCreateRequestDto;
import com.int_solutions.sys.dto.budgetDto.BudgetRespondRequestDto;
import com.int_solutions.sys.dto.budgetDto.BudgetResponseDto;
import com.int_solutions.sys.service.budgetService.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordenes/{ordenId}/presupuesto")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<BudgetResponseDto> emitirPresupuesto(
            @PathVariable Long ordenId,
            @Valid @RequestBody BudgetCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.emitirPresupuesto(ordenId, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<BudgetResponseDto> consultarPresupuesto(@PathVariable Long ordenId) {
        return ResponseEntity.ok(budgetService.consultarPresupuesto(ordenId));
    }

    @GetMapping("/repuestos-aprobados")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<List<ApprovedSparePartDto>> listarRepuestosAprobados(@PathVariable Long ordenId) {
        return ResponseEntity.ok(budgetService.listarRepuestosAprobados(ordenId));
    }

    // 3. Cliente aprueba o rechaza en línea
    @PatchMapping("/responder")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<BudgetResponseDto> responderPresupuesto(
            @PathVariable Long ordenId,
            @Valid @RequestBody BudgetRespondRequestDto request) {
        return ResponseEntity.ok(budgetService.responderPresupuesto(ordenId, request));
    }
}