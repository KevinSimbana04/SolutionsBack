package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.config.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParameterRepository extends JpaRepository<Parameter, Long> {
    Optional<Parameter> findByClave(String clave);
    boolean existsByClave(String clave);
}
