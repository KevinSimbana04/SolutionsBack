package com.int_solutions.sys.controller;

import com.int_solutions.sys.dto.equipmentDto.EquipmentRequestDto;
import com.int_solutions.sys.dto.equipmentDto.EquipmentResponseDto;
import com.int_solutions.sys.service.equipmentService.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipos")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('EQUIPO_CREAR') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<EquipmentResponseDto> registrarEquipo(@Valid @RequestBody EquipmentRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipmentService.registrarEquipo(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EQUIPO_LEER') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<List<EquipmentResponseDto>> listarEquipos(
            @RequestParam(value = "clienteId", required = false) Long clienteId) {
        return ResponseEntity.ok(equipmentService.listarEquipos(clienteId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPO_LEER') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<EquipmentResponseDto> obtenerEquipo(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPO_EDITAR') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<EquipmentResponseDto> actualizarEquipo(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentRequestDto request) {
        return ResponseEntity.ok(equipmentService.actualizarEquipo(id, request));
    }
}
