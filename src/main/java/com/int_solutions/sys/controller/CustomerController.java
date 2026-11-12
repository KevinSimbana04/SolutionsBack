package com.int_solutions.sys.controller;

import com.int_solutions.sys.dto.userDto.customerDto.CustomerRequestDto;
import com.int_solutions.sys.dto.userDto.customerDto.CustomerResponseDto;
import com.int_solutions.sys.service.customerService.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTE_CREAR') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<CustomerResponseDto> crearCliente(@Valid @RequestBody CustomerRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.crearCliente(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENTE_LEER') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<List<CustomerResponseDto>> listarClientes(
            @RequestParam(value = "buscar", required = false) String buscar) {
        return ResponseEntity.ok(customerService.listarClientes(buscar));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_LEER') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<CustomerResponseDto> obtenerCliente(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_EDITAR') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<CustomerResponseDto> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto request) {
        return ResponseEntity.ok(customerService.actualizarCliente(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAuthority('CLIENTE_DESACTIVAR') or hasAnyRole('ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<?> desactivarCliente(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String motivo = body.get("motivo");
        customerService.desactivarCliente(id, motivo);
        return ResponseEntity.ok(Map.of("mensaje", "Cuenta de usuario desactivada exitosamente"));
    }
}
