package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.payment.Payment;
import com.int_solutions.sys.entity.payment.PaymentStatusE;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    long countByNumeroReciboStartingWith(String prefijo);

    List<Payment> findByOrdenTrabajo_OrdenIdOrderByFechaPagoDesc(Long ordenId);

    List<Payment> findByOrdenTrabajo_OrdenIdAndEstadoPagoOrderByFechaPagoAsc(
            Long ordenId,
            PaymentStatusE estadoPago
    );

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Payment p WHERE p.ordenTrabajo.ordenId = :ordenId AND p.estadoPago = 'ACTIVO'")
    BigDecimal sumarTotalAbonadoActivo(@Param("ordenId") Long ordenId);

    @Override
    @EntityGraph(attributePaths = {
            "ordenTrabajo",
            "ordenTrabajo.cliente",
            "ordenTrabajo.equipo",
            "ordenTrabajo.estadoOrden",
            "usuarioRegistro"
    })
    List<Payment> findAll(Specification<Payment> spec, Sort sort);
}