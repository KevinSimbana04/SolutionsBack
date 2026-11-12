package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.auth.Role;
import com.int_solutions.sys.entity.auth.RoleE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNombreRol(RoleE nombreRol);
    boolean existsByNombreRol(RoleE nombreRol);
}
