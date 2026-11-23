package com.int_solutions.sys.repository.specification;

import com.int_solutions.sys.dto.filterDto.OrderFilterDto;
import com.int_solutions.sys.entity.workOrder.Order;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public  final class OrderSpecification {
    private OrderSpecification(){}

    public  static Specification<Order> conFiltros(OrderFilterDto filtro){
        return (root, query , cb)->{

            List<Predicate> predicateList= new ArrayList<>();

            //Asignar fitros
            if (filtro.tecnicoId() != null) {
                predicateList.add(cb.equal(root.get("tecnico").get("empleadoId"), filtro.tecnicoId()));
            }
            if (filtro.clienteId() != null) {
                predicateList.add(cb.equal(root.get("cliente").get("clienteId"), filtro.clienteId()));
            }
            if (filtro.estado() != null) {
                predicateList.add(cb.equal(root.get("estadoOrden").get("tipoEstado"), filtro.estado()));
            }
            if (filtro.busqueda() != null && !filtro.busqueda().isBlank()) {
                String patron = "%" + filtro.busqueda().trim().toLowerCase() + "%";
                Predicate porCodigo = cb.like(cb.lower(root.get("codigo")), patron);
                Predicate porCedula = cb.like(cb.lower(root.get("cliente").get("identificacion")), patron);
                Predicate porNombre = cb.like(cb.lower(root.get("cliente").get("nombres")), patron);
                Predicate porApellido = cb.like(cb.lower(root.get("cliente").get("apellidos")), patron);
                predicateList.add(cb.or(porCodigo, porCedula, porNombre, porApellido));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
