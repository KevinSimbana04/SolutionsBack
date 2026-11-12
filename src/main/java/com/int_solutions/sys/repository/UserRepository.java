package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNombreUsuario(String nombreUsuario);
    boolean existsByCorreo(String correo);
    Optional<User> findByNombreUsuario(String nombreUsuario);
    Optional<User> findByCorreo(String correo);
}
