package com.int_solutions.sys.service.authService;

import com.int_solutions.sys.dto.authDto.AuthRequestDto;
import com.int_solutions.sys.dto.authDto.AuthResponseDto;
import com.int_solutions.sys.dto.authDto.ConfigureCredentialsRequestDto;
import com.int_solutions.sys.entity.auth.RoleE;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.repository.UserRepository;
import com.int_solutions.sys.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImp implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_INTENTOS = 5;
    private static final long MINUTOS_BLOQUEO = 15;

    @Override
    @Transactional(noRollbackFor = {BadCredentialsException.class, LockedException.class})
    public AuthResponseDto login(AuthRequestDto request) {

        User user = userRepository.findByNombreUsuario(request.getNombreUsuario())
                .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));

        if (Boolean.TRUE.equals(user.getBloqueado())) {
            if (user.getFechaBloqueo() != null) {
                long minutosTranscurridos = Duration.between(user.getFechaBloqueo(), LocalDateTime.now()).toMinutes();
                if (minutosTranscurridos >= MINUTOS_BLOQUEO) {
                    user.setBloqueado(false);
                    user.setIntentosFallidos(0);
                    user.setFechaBloqueo(null);
                    userRepository.save(user);
                } else {
                    long restantes = MINUTOS_BLOQUEO - minutosTranscurridos;
                    throw new LockedException("Cuenta bloqueada temporalmente por seguridad tras 5 intentos fallidos. Intente nuevamente en " + restantes + " minutos.");
                }
            } else {
                throw new LockedException("Cuenta bloqueada temporalmente por seguridad tras 5 intentos fallidos. Intente nuevamente en 15 minutos.");
            }
        }

        if (!Boolean.TRUE.equals(user.getEstadoCuenta())) {
            throw new DisabledException("La cuenta se encuentra inactiva o pendiente de activación por correo.");
        }

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getNombreUsuario(), request.getContrasenia())
            );

            RoleE rolPrincipal = RoleE.CLIENTE;
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                rolPrincipal = user.getRoles().iterator().next().getNombreRol();
            }

            if (Boolean.TRUE.equals(user.getRequiereCambioClave())) {
                log.info("Primer inicio de sesión detectado para usuario [{}]. Se requiere actualización de contraseña.", user.getNombreUsuario());
                return AuthResponseDto.builder()
                        .token(null)
                        .nombreUsuario(user.getNombreUsuario())
                        .rol(rolPrincipal.name())
                        .tiempoExpiracionMinutos(0)
                        .requiereCambioClave(true)
                        .mensaje("Inicio de sesión exitoso. Se requiere cambio obligatorio de credenciales.")
                        .build();
            }

            user.setIntentosFallidos(0);
            user.setFechaBloqueo(null);
            user.setUltimoAcceso(LocalDateTime.now());
            userRepository.save(user);

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getNombreUsuario());

            String token = jwtUtil.generarToken(userDetails, rolPrincipal);
            int duracionMinutos = (int) (jwtUtil.obtenerTiempoExpiracion(rolPrincipal) / (1000 * 60));

            return AuthResponseDto.builder()
                    .token(token)
                    .nombreUsuario(userDetails.getUsername())
                    .rol(rolPrincipal.name())
                    .tiempoExpiracionMinutos(duracionMinutos)
                    .requiereCambioClave(false)
                    .mensaje("Inicio de sesión exitoso")
                    .build();

        } catch (BadCredentialsException e) {

            int intentos = user.getIntentosFallidos() + 1;
            user.setIntentosFallidos(intentos);

            if (intentos >= MAX_INTENTOS) {
                user.setBloqueado(true);
                user.setFechaBloqueo(LocalDateTime.now());
                userRepository.save(user);
                throw new LockedException("Cuenta bloqueada temporalmente tras 5 intentos fallidos. Intente nuevamente en 15 minutos.");
            }

            userRepository.save(user);
            throw new BadCredentialsException("Credenciales incorrectas. Intentos restantes: " + (MAX_INTENTOS - intentos));
        }
    }

    @Override
    @Transactional
    public void configurarCredencialesIniciales(ConfigureCredentialsRequestDto request) {
        User user = userRepository.findByNombreUsuario(request.getUsuarioActual())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con identificación: " + request.getUsuarioActual()));

        if (!passwordEncoder.matches(request.getContraseniaActual(), user.getContrasenia())) {
            throw new IllegalArgumentException("La contraseña actual ingresada es incorrecta");
        }

        if (!Boolean.TRUE.equals(user.getRequiereCambioClave())) {
            throw new IllegalArgumentException("Esta cuenta ya configuró sus credenciales previamente");
        }

        // Validar que la nueva contraseña no sea igual a la contraseña temporal
        if (passwordEncoder.matches(request.getNuevaContrasenia(), user.getContrasenia())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la contraseña temporal");
        }

        user.setContrasenia(passwordEncoder.encode(request.getNuevaContrasenia()));
        user.setRequiereCambioClave(false);
        user.setIntentosFallidos(0);
        user.setBloqueado(false);

        userRepository.save(user);
        log.info("Contraseña de primer ingreso actualizada exitosamente para usuario [{}]", user.getNombreUsuario());
    }
}
