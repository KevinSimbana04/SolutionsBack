package com.int_solutions.sys.mapper;

import com.int_solutions.sys.dto.userDto.employeeDto.EmployeeRequestDto;
import com.int_solutions.sys.dto.userDto.employeeDto.EmployeeResponseDto;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "employee.empleadoId", target = "empleadoId")
    @Mapping(source = "employee.usuario.usuarioId", target = "usuarioId")
    @Mapping(source = "employee.identificacion", target = "identificacion")
    @Mapping(source = "employee.nombres", target = "nombres")
    @Mapping(source = "employee.apellidos", target = "apellidos")
    @Mapping(source = "employee.telefono", target = "telefono")
    @Mapping(source = "employee.direccion", target = "direccion")
    @Mapping(source = "employee.cargo", target = "cargo")
    @Mapping(source = "employee.usuario.correo", target = "correo")
    @Mapping(source = "employee.usuario.estadoCuenta", target = "estadoCuenta")
    @Mapping(source = "employee.usuario.bloqueado", target = "bloqueado")
    @Mapping(source = "employee.usuario.fechaCreacion", target = "fechaCreacion")
    @Mapping(target = "rol", expression = "java(obtenerNombreRol(employee.getUsuario()))")
    @Mapping(source = "mensajeInyectado", target = "mensaje")
    EmployeeResponseDto toResponseDto(Employee employee, String mensajeInyectado);

    default EmployeeResponseDto toResponseDtoSinMensaje(Employee employee) {
        return toResponseDto(employee, null);
    }

    List<EmployeeResponseDto> toResponseDtoList(List<Employee> employees);

    @Mapping(target = "empleadoId", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Employee toEntity(EmployeeRequestDto request);

    default String obtenerNombreRol(User user) {
        if (user != null && user.getRoles() != null && !user.getRoles().isEmpty()) {
            return user.getRoles().iterator().next().getNombreRol().name();
        }
        return "SIN_ROL";
    }
}
