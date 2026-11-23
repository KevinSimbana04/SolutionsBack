package com.int_solutions.sys.service.repairService;

import com.int_solutions.sys.dto.repairDto.RepairRequestDto;
import com.int_solutions.sys.dto.repairDto.RepairResponseDto;

import java.util.List;

public interface RepairService {
    RepairResponseDto registrarAvanceReparacion(Long ordenId, RepairRequestDto request);
    List<RepairResponseDto> listarPorOrden(Long ordenId);
}
