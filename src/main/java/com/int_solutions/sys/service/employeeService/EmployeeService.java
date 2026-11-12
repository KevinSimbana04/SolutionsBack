package com.int_solutions.sys.service.employeeService;

import com.int_solutions.sys.dto.userDto.employeeDto.EmployeeRequestDto;
import com.int_solutions.sys.dto.userDto.employeeDto.EmployeeResponseDto;

import java.util.List;

public interface EmployeeService {
    EmployeeResponseDto crearEmpleado(EmployeeRequestDto request);
    List<EmployeeResponseDto> listarEmpleados();
    EmployeeResponseDto obtenerPorId(Long id);
    EmployeeResponseDto actualizarEmpleado(Long id, EmployeeRequestDto request);
    void desactivarEmpleado(Long id, String motivo);
}
