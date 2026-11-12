package com.int_solutions.sys.security;

import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException{
        User usuario= userRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado" + nombreUsuario));

        Set<GrantedAuthority> authorities = new HashSet<>();

        // Roles
        if (usuario.getRoles() != null){
            usuario.getRoles().forEach(rol ->{
                authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol().name()));

                //Asignar permisos
                if(rol.getPermisos() != null){
                    rol.getPermisos().forEach(permiso ->{
                        authorities.add(new SimpleGrantedAuthority(permiso.getNombrePermiso().name()));
                    });
                }
            });
        }

        return new org.springframework.security.core.userdetails.User(
                usuario.getNombreUsuario(),
                usuario.getContrasenia(),
                Boolean.TRUE.equals(usuario.getEstadoCuenta()),
                true,
                true,
                !Boolean.TRUE.equals(usuario.getBloqueado()),
                authorities
        );
    }

}
