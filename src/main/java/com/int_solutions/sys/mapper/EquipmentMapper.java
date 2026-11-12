package com.int_solutions.sys.mapper;

import com.int_solutions.sys.dto.equipmentDto.EquipmentResponseDto;
import com.int_solutions.sys.entity.equipment.Equipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {

    @Mapping(source = "equipment.equipoId", target = "equipoId")
    @Mapping(source = "equipment.cliente.clienteId", target = "clienteId")
    @Mapping(target = "nombreCliente", expression = "java(equipment.getCliente() != null ? equipment.getCliente().getNombres() + \" \" + equipment.getCliente().getApellidos() : null)")
    @Mapping(source = "equipment.cliente.identificacion", target = "cedulaCliente")
    @Mapping(target = "tipoEquipo", expression = "java(equipment.getTipoEquipo() != null && equipment.getTipoEquipo().getNombreEquipo() != null ? equipment.getTipoEquipo().getNombreEquipo().name() : null)")
    @Mapping(source = "equipment.marca", target = "marca")
    @Mapping(source = "equipment.modelo", target = "modelo")
    @Mapping(source = "equipment.numeroSerie", target = "numeroSerie")
    @Mapping(source = "equipment.color", target = "color")
    @Mapping(source = "equipment.accesorios", target = "accesorios")
    @Mapping(source = "equipment.estadoFisicoInicial", target = "estadoFisicoInicial")
    @Mapping(source = "equipment.fotografiaInicial", target = "fotografiaInicial")
    @Mapping(source = "equipment.fallaReportada", target = "fallaReportada")
    @Mapping(source = "equipment.observaciones", target = "observaciones")
    @Mapping(source = "mensajeInyectado", target = "mensaje")
    EquipmentResponseDto toResponseDto(Equipment equipment, String mensajeInyectado);

    default EquipmentResponseDto toResponseDtoSinMensaje(Equipment equipment) {
        return toResponseDto(equipment, null);
    }

    List<EquipmentResponseDto> toResponseDtoList(List<Equipment> equipments);
}
