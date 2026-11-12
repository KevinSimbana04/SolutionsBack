package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.auth.Permission;
import com.int_solutions.sys.entity.auth.PermissionE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByNombrePermiso(PermissionE nombrePermiso);
    boolean existsByNombrePermiso(PermissionE nombrePermiso);
}
