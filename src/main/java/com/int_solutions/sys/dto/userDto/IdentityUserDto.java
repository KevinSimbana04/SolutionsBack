package com.int_solutions.sys.dto.userDto;

import com.int_solutions.sys.entity.user.User;
import lombok.Builder;

@Builder
public record IdentityUserDto(
        User usuario,
        boolean esNuevoUsuario,
        String claveTemporal,
        String tokenActivacion
) {
}
