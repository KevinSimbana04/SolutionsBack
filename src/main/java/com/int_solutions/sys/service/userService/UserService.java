package com.int_solutions.sys.service.userService;

import com.int_solutions.sys.dto.userDto.IdentityUserDto;
import com.int_solutions.sys.entity.auth.RoleE;

public interface UserService {
    String activarCuenta(String token);
    public IdentityUserDto registrarRolUsuario(String idenetificacion , String correo, RoleE nombreRol);
}
