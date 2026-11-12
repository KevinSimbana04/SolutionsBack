package com.int_solutions.sys.service.userService;

import com.int_solutions.sys.dto.userDto.IdentityUserDto;
import com.int_solutions.sys.entity.auth.Role;
import com.int_solutions.sys.entity.auth.RoleE;
import com.int_solutions.sys.entity.user.ActivateToken;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.repository.ActivateTokenRepository;
import com.int_solutions.sys.repository.RoleRepository;
import com.int_solutions.sys.repository.UserRepository;
import com.int_solutions.sys.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final ActivateTokenRepository activateTokenRepository;
    private final UserRepository userRepository;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public String activarCuenta(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("El enlace de activación no es válido.");
        }

        ActivateToken activateToken = activateTokenRepository.findByToken(token.trim())
                .orElseThrow(() -> new IllegalArgumentException("El enlace de activación no es válido."));

        if (Boolean.TRUE.equals(activateToken.getUsado())) {
            throw new IllegalArgumentException("Este enlace de activación ya fue utilizado.");
        }
        if (activateToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El enlace de activación ha expirado.");
        }

        User user = activateToken.getUsuario();
        user.setEstadoCuenta(true);
        user.setBloqueado(false);
        user.setIntentosFallidos(0);
        userRepository.save(user);

        activateToken.setUsado(true);
        activateTokenRepository.save(activateToken);

        return user.getNombreUsuario();
    }

    @Override
    @Transactional
    public IdentityUserDto registrarRolUsuario(String identificacion, String correo , RoleE nombreRol){
        Optional<User> usuarioExistenteOpt = userRepository.findByNombreUsuario(identificacion);
        if (usuarioExistenteOpt.isEmpty()) {
            usuarioExistenteOpt = userRepository.findByCorreo(correo);
        }

        Role rolSolicitado = roleRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("El rol no encontrado"));

        if (usuarioExistenteOpt.isPresent()) {
            User usuario = usuarioExistenteOpt.get();
            boolean yaTieneRol = usuario.getRoles().stream()
                    .anyMatch(r -> r.getNombreRol().equals(nombreRol));

            if (yaTieneRol) {
                throw new IllegalArgumentException("Este usuario ya tiene el rol " + nombreRol + " asignado.");
            }

            usuario.getRoles().add(rolSolicitado);
            return IdentityUserDto.builder()
                    .usuario(userRepository.save(usuario))
                    .esNuevoUsuario(false)
                    .build();
        } else {
            String claveTemporal = passwordGenerator.generarpassword(10);
            User nuevoUsuario = User.builder()
                    .nombreUsuario(identificacion)
                    .correo(correo)
                    .contrasenia(passwordEncoder.encode(claveTemporal))
                    .estadoCuenta(false)
                    .bloqueado(false)
                    .intentosFallidos(0)
                    .fechaCreacion(LocalDate.now())
                    .requiereCambioClave(true)
                    .build();
            nuevoUsuario.getRoles().add(rolSolicitado);

            User usuarioGuardado = userRepository.save(nuevoUsuario);

            String tokenStr = UUID.randomUUID().toString();
            ActivateToken tokenActivacion = ActivateToken.builder()
                    .token(tokenStr)
                    .fechaExpiracion(LocalDateTime.now().plusHours(24))
                    .usado(false)
                    .usuario(usuarioGuardado)
                    .build();
            activateTokenRepository.save(tokenActivacion);

            return IdentityUserDto.builder()
                    .usuario(usuarioGuardado)
                    .esNuevoUsuario(true)
                    .claveTemporal(claveTemporal)
                    .tokenActivacion(tokenStr)
                    .build();
        }
    }
}
