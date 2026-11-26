package com.int_solutions.sys.repository.specification;

import com.int_solutions.sys.dto.filterDto.PaymentFilterDto;
import com.int_solutions.sys.entity.payment.Payment;
import com.int_solutions.sys.entity.payment.PaymentStatusE;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class PaymentSpecification {

    private PaymentSpecification() {}

    public static Specification<Payment> conFiltros(PaymentFilterDto filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Para totales de recaudación solo contamos pagos activos
            predicates.add(cb.equal(root.get("estadoPago"), PaymentStatusE.ACTIVO));

            if (filtro.fechaInicio() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("fechaPago"),
                        filtro.fechaInicio().atStartOfDay()
                ));
            }
            if (filtro.fechaFin() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("fechaPago"),
                        filtro.fechaFin().atTime(LocalTime.MAX)
                ));
            }
            if (filtro.metodoPago() != null) {
                predicates.add(cb.equal(root.get("metodoPago"), filtro.metodoPago()));
            }
            if (filtro.estadoOrden() != null) {
                predicates.add(cb.equal(
                        root.get("ordenTrabajo").get("estadoOrden").get("tipoEstado"),
                        filtro.estadoOrden()
                ));
            }
            if (filtro.busqueda() != null && !filtro.busqueda().isBlank()) {
                String patron = "%" + filtro.busqueda().trim().toLowerCase() + "%";
                Predicate porCodigoOt = cb.like(cb.lower(root.get("ordenTrabajo").get("codigo")), patron);
                Predicate porRecibo = cb.like(cb.lower(root.get("numeroRecibo")), patron);
                Predicate porCedula = cb.like(cb.lower(root.get("ordenTrabajo").get("cliente").get("identificacion")), patron);
                Predicate porNombre = cb.like(cb.lower(root.get("ordenTrabajo").get("cliente").get("nombres")), patron);
                Predicate porApellido = cb.like(cb.lower(root.get("ordenTrabajo").get("cliente").get("apellidos")), patron);
                predicates.add(cb.or(porCodigoOt, porRecibo, porCedula, porNombre, porApellido));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}