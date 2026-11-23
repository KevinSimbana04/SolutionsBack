package com.int_solutions.sys.util;

import com.int_solutions.sys.entity.user.Customer;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.repository.CustomerRepository;
import com.int_solutions.sys.repository.EmployeeRepository;
import com.int_solutions.sys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityContextHelper {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;

    public User obtenerUsuarioEnSesion() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new IllegalStateException("Usuario en sesión no encontrado."));
    }

    public Employee obtenerEmpleadoEnSesion() {
        User usuario = obtenerUsuarioEnSesion();
        return employeeRepository.findByUsuario_UsuarioId(usuario.getUsuarioId())
                .orElseThrow(() -> new IllegalStateException("El usuario en sesión no tiene perfil de empleado."));
    }

    public Customer obtenerClienteEnSesion() {
        User usuario = obtenerUsuarioEnSesion();
        return customerRepository.findByUsuario_UsuarioId(usuario.getUsuarioId())
                .orElseThrow(() -> new IllegalStateException("El usuario en sesión no tiene perfil de cliente."));
    }
}
