package com.int_solutions.sys.util;

import com.int_solutions.sys.entity.auth.Permission;
import com.int_solutions.sys.entity.auth.PermissionE;
import com.int_solutions.sys.entity.auth.Role;
import com.int_solutions.sys.entity.auth.RoleE;
import com.int_solutions.sys.repository.PermissionRepository;
import com.int_solutions.sys.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class RolePermissionSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args){
        log.info("Iniciando cotalogo de permisos y roles ...");

        //Guarda todos lo permiso en la base de datos
        for(PermissionE pe: PermissionE.values()){
            if(!permissionRepository.existsByNombrePermiso(pe)){
                permissionRepository.save(Permission.builder()
                        .nombrePermiso(pe)
                        .build());
            }
        }

        for(RoleE role: RoleE.values()){
            if(!roleRepository.existsByNombreRol(role)){
                Role rol = Role.builder()
                        .nombreRol(role)
                        .build();
                rol.setPermisos(mapperServiceForRol(role));
                roleRepository.save(rol);

                log.info("Rol inicializado: {}", role.name());
            }
        }
    }

    private Set<Permission> mapperServiceForRol(RoleE rol){
        Set<PermissionE> enums = new HashSet<>();

        switch (rol){
            case ADMINISTRADOR:
                enums.add(PermissionE.USUARIO_CREAR);
                enums.add(PermissionE.USUARIO_LEER);
                enums.add(PermissionE.USUARIO_EDITAR);
                enums.add(PermissionE.USUARIO_DESACTIVAR);
                enums.add(PermissionE.ROL_ASIGNAR);
                enums.add(PermissionE.PARAMETRO_LEER);
                enums.add(PermissionE.PARAMERTRO_EDITAR);
                break;
            case ADMINISTRATIVO:
                enums.add(PermissionE.CLIENTE_CREAR);
                enums.add(PermissionE.CLIENTE_LEER);
                enums.add(PermissionE.CLIENTE_EDITAR);
                enums.add(PermissionE.CLIENTE_DESACTIVAR);
                enums.add(PermissionE.EQUIPO_CREAR);
                enums.add(PermissionE.EQUIPO_LEER);
                enums.add(PermissionE.EQUIPO_EDITAR);
                enums.add(PermissionE.ORDEN_CREAR);
                enums.add(PermissionE.ORDDEN_LEER_TODAS);
                enums.add(PermissionE.ORDEN_EDITAR_RECEPCION);
                enums.add(PermissionE.ORDEN_ASIGNAR_TECNICO);
                enums.add(PermissionE.ORDEN_CANCELAR);

                break;
            case TECNICO:
                enums.add(PermissionE.ORDEN_CONFIRMAR_RECEPCION);
                enums.add(PermissionE.ORDEN_INICIAR_DIAGNOSTICO);

                break;
            case CLIENTE:
                enums.add(PermissionE.ORDEN_LEER_PROPIAS);
                enums.add(PermissionE.EQUIPO_LEER_PROPIOS);
                break;
        }

        Set<Permission> permissionsEntity= new HashSet<>();
        for (PermissionE pe : enums){
            permissionRepository.findByNombrePermiso(pe).ifPresent(permissionsEntity::add);
        }
        return permissionsEntity;
    }
}
