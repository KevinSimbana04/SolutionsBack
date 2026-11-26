package com.int_solutions.sys.mapper;

import com.int_solutions.sys.dto.reviewDto.ReviewCreateRequestDto;
import com.int_solutions.sys.dto.reviewDto.ReviewResponseDto;
import com.int_solutions.sys.entity.review.Review;
import com.int_solutions.sys.entity.user.Customer;
import com.int_solutions.sys.entity.user.Employee;
import com.int_solutions.sys.entity.workOrder.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    //Datos de entrada

    @Mapping(target = "resenaId", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(source = "orden", target = "ordenTrabajo")
    @Mapping(source = "cliente", target = "cliente")
    @Mapping(source = "tecnico", target = "tecnico")
    Review toEntity(ReviewCreateRequestDto request, Order orden, Customer cliente, Employee tecnico);

    //Datos de salida
    @Mapping(source = "ordenTrabajo.ordenId", target = "ordenId")
    @Mapping(source = "ordenTrabajo.codigo", target = "codigoOrden")
    @Mapping(target = "tecnicoNombreCompleto",
            expression = "java(review.getTecnico() != null ? review.getTecnico().getNombres() + \" \" + review.getTecnico().getApellidos() : null)")
    ReviewResponseDto toResponseDto(Review review);
}
