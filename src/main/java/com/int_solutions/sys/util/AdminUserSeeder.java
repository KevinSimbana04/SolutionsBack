package com.int_solutions.sys.util;

import com.int_solutions.sys.entity.auth.Role;
import com.int_solutions.sys.entity.auth.RoleE;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.repository.RoleRepository;
import com.int_solutions.sys.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class AdminUserSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    String usernameAdmin;
    @Value("${app.admin.password}")
    String passwordAdmin;


    @Override
    @Transactional
    public void run (String... args){

        if(!userRepository.existsByNombreUsuario(usernameAdmin)) {
            log.info("Generando usuario adiminsitrador ...........");

            Role rolAdmin = roleRepository.findByNombreRol(RoleE.ADMINISTRADOR)
                    .orElseThrow(() -> new RuntimeException("El rol administrador debe existir previamente en BDD"));


            User userAdmin = User.builder()
                    .nombreUsuario(usernameAdmin)
                    .correo("admin_inicial@int-solutions.com")
                    .contrasenia(passwordEncoder.encode(passwordAdmin))
                    .estadoCuenta(true)
                    .requiereCambioClave(false)
                    .intentosFallidos(0)
                    .build();

            userAdmin.getRoles().add(rolAdmin);
            User saveUser = userRepository.save(userAdmin);
        }
        log.info("Usuario Administrador generado correactamente");
    }
}
