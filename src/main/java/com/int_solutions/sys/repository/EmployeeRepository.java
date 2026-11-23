package com.int_solutions.sys.repository;

import com.int_solutions.sys.dto.userDto.employeeDto.TechnicalResponseDto;
import com.int_solutions.sys.entity.auth.RoleE;
import com.int_solutions.sys.entity.user.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByIdentificacion(String identificacion);
    Optional<Employee> findByIdentificacion(String identificacion);
    Optional<Employee> findByUsuario_UsuarioId(Long usuarioId);

    @Query("""
    SELECT new com.int_solutions.sys.dto.userDto.employeeDto.TechnicalResponseDto(
        e.empleadoId, e.nombres, e.apellidos, e.cargo
    )
    FROM Employee e
    JOIN e.usuario u
    JOIN u.roles r
    WHERE r.nombreRol = :rol
      AND u.estadoCuenta = true
      AND u.bloqueado = false
    ORDER BY e.apellidos, e.nombres
    """)
    List<TechnicalResponseDto> findTecnicosActivos(@Param("rol") RoleE rol);
}
