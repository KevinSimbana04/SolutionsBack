package com.int_solutions.sys.service.diagnosisService;

import com.int_solutions.sys.dto.diagnosisDto.DiagnosisRequestDto;
import com.int_solutions.sys.dto.diagnosisDto.DiagnosisResponseDto;

public interface DiagnosisService {
    DiagnosisResponseDto emitirDiagnostico(Long ordenId, DiagnosisRequestDto request);
    DiagnosisResponseDto obtenerPorOrden(Long ordenId);
}
