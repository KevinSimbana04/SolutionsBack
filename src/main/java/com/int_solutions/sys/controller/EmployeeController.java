package com.int_solutions.sys.controller;

import com.int_solutions.sys.dto.userDto.employeeDto.EmployeeRequestDto;
import com.int_solutions.sys.dto.userDto.employeeDto.EmployeeResponseDto;
import com.int_solutions.sys.service.employeeService.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/empleados")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAuthority('USUARIO_CREAR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<EmployeeResponseDto> crearEmpleado(@Valid @RequestBody EmployeeRequestDto request) {
        EmployeeResponseDto response = employeeService.crearEmpleado(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USUARIO_LEER') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<EmployeeResponseDto>> listarEmpleados() {
        return ResponseEntity.ok(employeeService.listarEmpleados());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_LEER') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<EmployeeResponseDto> obtenerEmpleado(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_EDITAR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<EmployeeResponseDto> actualizarEmpleado(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDto request) {
        return ResponseEntity.ok(employeeService.actualizarEmpleado(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('USUARIO_DESACTIVAR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> desactivarEmpleado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String motivo = body.get("motivo");
        employeeService.desactivarEmpleado(id, motivo);
        return ResponseEntity.ok(Map.of("mensaje", "Cuenta de usuario desactivada exitosamente"));
    }
}
