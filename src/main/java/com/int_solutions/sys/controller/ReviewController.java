package com.int_solutions.sys.controller;

import com.int_solutions.sys.dto.reviewDto.ReviewCreateRequestDto;
import com.int_solutions.sys.dto.reviewDto.ReviewResponseDto;
import com.int_solutions.sys.service.reviewService.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordenes/{ordenId}/resena")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasAuthority('RESENA_CREAR')  or hasRole('CLIENTE')")
    public ResponseEntity<ReviewResponseDto> crearResena(
            @PathVariable Long ordenId,
            @Valid @RequestBody ReviewCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.crearResena(ordenId, request));
    }

    @GetMapping
    @PreAuthorize(" hasAuthority('RESENA_LEER') or hasAnyRole('CLIENTE', 'ADMINISTRATIVO', 'ADMINISTRADOR')")
    public ResponseEntity<ReviewResponseDto> obtenerResena(@PathVariable Long ordenId) {
        return ResponseEntity.ok(reviewService.obtenerPorOrden(ordenId));
    }
}
