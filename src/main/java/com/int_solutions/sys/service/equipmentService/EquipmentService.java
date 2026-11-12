package com.int_solutions.sys.service.equipmentService;

import com.int_solutions.sys.dto.equipmentDto.EquipmentRequestDto;
import com.int_solutions.sys.dto.equipmentDto.EquipmentResponseDto;

import java.util.List;

public interface EquipmentService {
    EquipmentResponseDto registrarEquipo(EquipmentRequestDto request);
    List<EquipmentResponseDto> listarEquipos(Long clienteId);
    EquipmentResponseDto obtenerPorId(Long id);
    EquipmentResponseDto actualizarEquipo(Long id, EquipmentRequestDto request);
}
