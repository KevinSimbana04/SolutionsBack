package com.int_solutions.sys.service.customerService;

import com.int_solutions.sys.dto.userDto.customerDto.CustomerRequestDto;
import com.int_solutions.sys.dto.userDto.customerDto.CustomerResponseDto;

import java.util.List;

public interface CustomerService {
    CustomerResponseDto crearCliente(CustomerRequestDto request);
    List<CustomerResponseDto> listarClientes(String buscar);
    CustomerResponseDto obtenerPorId(Long id);
    CustomerResponseDto actualizarCliente(Long id, CustomerRequestDto request);
    void desactivarCliente(Long id, String motivo);
}
