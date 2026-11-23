package com.int_solutions.sys.mapper;

import com.int_solutions.sys.dto.diagnosisDto.DiagnosisRequestDto;
import com.int_solutions.sys.dto.diagnosisDto.DiagnosisResponseDto;
import com.int_solutions.sys.entity.diagnosis.Diagnosis;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.workOrder.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiagnosisMapper {

    //Datos de entrada
    @Mapping(target = "diagnosticoId", ignore = true)
    @Mapping(target = "fechaEmision", ignore = true)
    @Mapping(source = "orden", target = "ordenTrabajo")
    @Mapping(source = "tecnico", target = "tecnico")
    @Mapping(target = "valorManoObra",
            expression = "java(\"TIENE_REPARACION\".equals(request.getPosibilidadesReparacion()) ? request.getValorManoObra() : java.math.BigDecimal.ZERO)")
    Diagnosis toEntity(DiagnosisRequestDto request, Order orden, Employee tecnico);


    //Datos de salida
    @Mapping(source = "diagnosticoId", target = "id")
    @Mapping(source = "ordenTrabajo.ordenId", target = "ordenId")
    @Mapping(source = "ordenTrabajo.codigo", target = "codigoOrden")
    @Mapping(target = "tecnicoNombreCompleto",
            expression = "java(diagnosis.getTecnico() != null ? diagnosis.getTecnico().getNombres() + \" \" + diagnosis.getTecnico().getApellidos() : null)")
    @Mapping(source = "ordenTrabajo.estadoOrden.tipoEstado", target = "nuevoEstadoOrden")
    DiagnosisResponseDto toResponseDto(Diagnosis diagnosis);
}