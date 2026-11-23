package com.int_solutions.sys.mapper;

import com.int_solutions.sys.dto.budgetDto.ApprovedSparePartDto;
import com.int_solutions.sys.dto.budgetDto.BudgetCreateRequestDto;
import com.int_solutions.sys.dto.budgetDto.BudgetResponseDto;
import com.int_solutions.sys.dto.budgetDto.SparePartItemDto;
import com.int_solutions.sys.entity.budget.Budget;
import com.int_solutions.sys.entity.budget.DetailSparePart;
import com.int_solutions.sys.entity.diagnosis.Diagnosis;
import com.int_solutions.sys.entity.workOrder.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    //Datos de Salida
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "costoRepuestos", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "valorIva", ignore = true)
    @Mapping(target = "totalPagar", ignore = true)
    @Mapping(target = "motivoRechazo", ignore = true)
    @Mapping(target = "detallesRepuesto", ignore = true)
    @Mapping(target = "estadoPresupuesto", constant = "PENDIENTE_APROBACION")
    @Mapping(target = "confirmacion", constant = "false")
    @Mapping(source = "orden", target = "ordenTrabajo")
    @Mapping(source = "diagnostico", target = "diagnostico")
    @Mapping(source = "request.costoManoObra", target = "costoManoObra")
    @Mapping(source = "fechaEnvio", target = "fechaEnvio")
    @Mapping(source = "fechaLimite", target = "fechaLimite")
    Budget toEntity(
            BudgetCreateRequestDto request,
            Order orden,
            Diagnosis diagnostico,
            LocalDateTime fechaEnvio,
            LocalDateTime fechaLimite
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "utilizadoReparacion", constant = "false")
    @Mapping(source = "presupuesto", target = "presupuesto")
    DetailSparePart toSparePartEntity(SparePartItemDto item, Budget presupuesto);


    //Datos de Salida
    @Mapping(source = "ordenTrabajo.ordenId", target = "ordenId")
    @Mapping(source = "ordenTrabajo.codigo", target = "codigoOrden")
    @Mapping(target = "equipoMarcaModelo",
            expression = "java(budget.getOrdenTrabajo() != null && budget.getOrdenTrabajo().getEquipo() != null ? budget.getOrdenTrabajo().getEquipo().getMarca() + \" \" + budget.getOrdenTrabajo().getEquipo().getModelo() : null)")
    @Mapping(source = "ordenTrabajo.fechaEstimadaEntrega", target = "fechaEstimadaEntrega")
    BudgetResponseDto toResponseDto(Budget budget);

    BudgetResponseDto.SparePartResponseDto toSparePartDto(DetailSparePart part);

    List<ApprovedSparePartDto> toApprovedSparePartDtoList(List<DetailSparePart> parts);
}
