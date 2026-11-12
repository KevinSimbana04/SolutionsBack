package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.user.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByIdentificacion(String identificacion);
    Optional<Employee> findByIdentificacion(String identificacion);
}
