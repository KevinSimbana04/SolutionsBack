package com.int_solutions.sys.service.parameterService;

import com.int_solutions.sys.dto.parameterDto.ParameterGeneralDto;

public interface ParameterService {
    ParameterGeneralDto obtenerParametros();
    ParameterGeneralDto actualizarParametros(ParameterGeneralDto request, String userName);
}
