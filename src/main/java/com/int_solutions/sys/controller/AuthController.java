package com.int_solutions.sys.controller;

import com.int_solutions.sys.dto.authDto.AuthRequestDto;
import com.int_solutions.sys.dto.authDto.AuthResponseDto;
import com.int_solutions.sys.dto.authDto.ConfigureCredentialsRequestDto;
import com.int_solutions.sys.security.TokenBlacklistService;
import com.int_solutions.sys.service.authService.AuthService;
import com.int_solutions.sys.service.userService.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            tokenBlacklistService.revocarToken(jwt);
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente. El token ha sido invalidado."));
    }

    @PostMapping("/configurar-credenciales")
    public ResponseEntity<?> configurarCredenciales(@Valid @RequestBody ConfigureCredentialsRequestDto request) {
        authService.configurarCredencialesIniciales(request);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Credenciales actualizadas exitosamente. Ya puede iniciar sesión con su nueva contraseña."
        ));
    }

    @PostMapping("/activar-cuenta")
    public ResponseEntity<?> activarCuenta(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String username = userService.activarCuenta(token);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Cuenta activada exitosamente. Por favor, inicie sesión con su usuario y contraseña temporal.",
                "nombreUsuario", username != null ? username : ""
        ));
    }
}
