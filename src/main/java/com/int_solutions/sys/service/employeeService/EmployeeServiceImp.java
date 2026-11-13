package com.int_solutions.sys.service.employeeService;

import com.int_solutions.sys.dto.userDto.IdentityUserDto;
import com.int_solutions.sys.dto.userDto.employeeDto.EmployeeRequestDto;
import com.int_solutions.sys.dto.userDto.employeeDto.EmployeeResponseDto;
import com.int_solutions.sys.entity.auth.Role;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.mapper.EmployeeMapper;
import com.int_solutions.sys.repository.EmployeeRepository;
import com.int_solutions.sys.repository.RoleRepository;
import com.int_solutions.sys.repository.UserRepository;
import com.int_solutions.sys.service.userService.EmailService;
import com.int_solutions.sys.service.userService.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImp implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final EmployeeMapper employeeMapper;
    private final UserService userService;

    @Override
    @Transactional
    public EmployeeResponseDto crearEmpleado(EmployeeRequestDto request) {
        IdentityUserDto identidad= userService.registrarRolUsuario(request.getIdentificacion(),
                request.getCorreo(),request.getRol());

        Employee nuevoEmpleado = Employee.builder()
                .identificacion(request.getIdentificacion())
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .cargo(request.getCargo())
                .usuario(identidad.usuario())
                .build();
        Employee empleadoGuardado = employeeRepository.save(nuevoEmpleado);

        if (identidad.esNuevoUsuario()) {
            String nombreCompleto = empleadoGuardado.getNombres() + " " + empleadoGuardado.getApellidos();
            emailService.enviarActivacionBrevo(
                    identidad.usuario().getCorreo(),
                    nombreCompleto,
                    identidad.usuario().getNombreUsuario(),
                    identidad.claveTemporal(),
                    identidad.tokenActivacion(),
                    "empleado (" + request.getCargo() + ")"
            );
            return employeeMapper.toResponseDto(empleadoGuardado, "Usuario registrado exitosamente. Correo enviado.");
        }

        return employeeMapper.toResponseDto(empleadoGuardado, "Rol de Empleado añadido.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> listarEmpleados() {
        return employeeMapper.toResponseDtoList(employeeRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto obtenerPorId(Long id) {
        Employee empleado = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con ID: " + id));
        return employeeMapper.toResponseDtoSinMensaje(empleado);
    }

    @Override
    @Transactional
    public EmployeeResponseDto actualizarEmpleado(Long id, EmployeeRequestDto request) {
        Employee empleado = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con ID: " + id));

        User usuario = empleado.getUsuario();

        // Validar unicidad de cédula o correo electrónico
        if (!empleado.getIdentificacion().equals(request.getIdentificacion()) &&
                employeeRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new IllegalArgumentException("el número de identificación o correo electrónico ya se encuentra registrado");
        }
        if (!usuario.getCorreo().equalsIgnoreCase(request.getCorreo()) &&
                userRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("el número de identificación o correo electrónico ya se encuentra registrado");
        }

        Role nuevoRol = roleRepository.findByNombreRol(request.getRol())
                .orElseThrow(() -> new IllegalArgumentException("El rol seleccionado no existe"));

        empleado.setIdentificacion(request.getIdentificacion());
        empleado.setNombres(request.getNombres());
        empleado.setApellidos(request.getApellidos());
        empleado.setTelefono(request.getTelefono());
        empleado.setDireccion(request.getDireccion());
        empleado.setCargo(request.getCargo());

        usuario.setCorreo(request.getCorreo());
        usuario.setNombreUsuario(request.getIdentificacion());
        usuario.getRoles().clear();
        usuario.getRoles().add(nuevoRol);

        userRepository.save(usuario);
        Employee empleadoActualizado = employeeRepository.save(empleado);

        log.info("Auditoría: Datos del empleado ID {} actualizados correctamente", id);

        return employeeMapper.toResponseDto(empleadoActualizado, "Datos del empleado actualizados correctamente");
    }

    @Override
    @Transactional
    public void desactivarEmpleado(Long id, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe confirmar el motivo para desactivar la cuenta");
        }
        Employee empleado = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con ID: " + id));

        User usuario = empleado.getUsuario();
        usuario.setEstadoCuenta(false);
        usuario.setMotivo(motivo.trim());
        userRepository.save(usuario);

        log.info("Auditoría: Cuenta de empleado ID {} desactivada lógicamente. Motivo: {}", id, motivo);
    }
}
