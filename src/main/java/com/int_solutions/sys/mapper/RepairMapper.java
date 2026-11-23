package com.int_solutions.sys.mapper;

import com.int_solutions.sys.dto.repairDto.RepairRequestDto;
import com.int_solutions.sys.dto.repairDto.RepairResponseDto;
import com.int_solutions.sys.entity.repair.Repair;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.workOrder.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RepairMapper {

    //Datos de entrada
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaInicio", ignore = true)
    @Mapping(source = "orden", target = "ordenTrabajo")
    @Mapping(source = "tecnico", target = "tecnico")
    @Mapping(source = "fechaFin", target = "fechaFin")
    Repair toEntity(RepairRequestDto request, Order orden, Employee tecnico, LocalDateTime fechaFin);


    //Datos de salida
    @Mapping(source = "ordenTrabajo.ordenId", target = "ordenId")
    @Mapping(source = "ordenTrabajo.codigo", target = "codigoOrden")
    @Mapping(target = "tecnicoNombreCompleto",
            expression = "java(repair.getTecnico() != null ? repair.getTecnico().getNombres() + \" \" + repair.getTecnico().getApellidos() : null)")
    @Mapping(source = "ordenTrabajo.estadoOrden.tipoEstado", target = "nuevoEstadoOrden")
    RepairResponseDto toResponseDto(Repair repair);
    List<RepairResponseDto> toResponseDtoList(List<Repair> repairs);
}