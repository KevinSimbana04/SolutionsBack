package com.int_solutions.sys.service.authService;

import com.int_solutions.sys.dto.authDto.AuthRequestDto;
import com.int_solutions.sys.dto.authDto.AuthResponseDto;
import com.int_solutions.sys.dto.authDto.ConfigureCredentialsRequestDto;

public interface AuthService {
    AuthResponseDto login(AuthRequestDto request);
    void  configurarCredencialesIniciales(ConfigureCredentialsRequestDto request);
}
