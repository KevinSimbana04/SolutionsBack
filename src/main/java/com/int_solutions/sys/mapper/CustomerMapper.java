package com.int_solutions.sys.mapper;

import com.int_solutions.sys.dto.userDto.customerDto.CustomerRequestDto;
import com.int_solutions.sys.dto.userDto.customerDto.CustomerResponseDto;
import com.int_solutions.sys.entity.user.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper     (componentModel = "spring")
public interface CustomerMapper {

    @Mapping(source = "customer.clienteId", target = "clienteId")
    @Mapping(source = "customer.usuario.usuarioId", target = "usuarioId")
    @Mapping(source = "customer.identificacion", target = "identificacion")
    @Mapping(source = "customer.nombres", target = "nombres")
    @Mapping(source = "customer.apellidos", target = "apellidos")
    @Mapping(source = "customer.telefono", target = "telefono")
    @Mapping(source = "customer.direccion", target = "direccion")
    @Mapping(source = "customer.usuario.correo", target = "correo")
    @Mapping(source = "customer.usuario.nombreUsuario", target = "nombreUsuario")
    @Mapping(target = "rol", constant = "CLIENTE")
    @Mapping(source = "customer.usuario.estadoCuenta", target = "estadoCuenta")
    @Mapping(source = "customer.usuario.motivo", target = "motivo")
    @Mapping(source = "customer.usuario.bloqueado", target = "bloqueado")
    @Mapping(source = "customer.usuario.fechaCreacion", target = "fechaCreacion")
    @Mapping(source = "mensajeInyectado", target = "mensaje")
    CustomerResponseDto toResponseDto(Customer customer, String mensajeInyectado);

    default CustomerResponseDto toResponseDtoSinMensaje(Customer customer) {
        return toResponseDto(customer, null);
    }

    List<CustomerResponseDto> toResponseDtoList(List<Customer> customers);

    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Customer toEntity(CustomerRequestDto request);
}
