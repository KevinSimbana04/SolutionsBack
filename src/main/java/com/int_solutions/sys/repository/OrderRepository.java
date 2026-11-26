package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.workOrder.Order;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    long countByCodigoStartingWith(String prefijo);

    @Override
    @EntityGraph(attributePaths = {"estadoOrden", "cliente", "tecnico", "equipo"})
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"estadoOrden", "cliente", "tecnico", "equipo"})
    List<Order> findAll(Specification<Order> spec, Sort sort);

    boolean existsByEquipo_EquipoIdAndEstadoOrden_TipoEstadoNotIn(
            Long equipoId,
            Collection<OrderStatusE> estadosFinalizados
    );

    @EntityGraph(attributePaths = {"estadoOrden", "equipo", "tecnico"})
    List<Order> findByEquipo_EquipoIdAndEstadoOrden_TipoEstadoInOrderByFechaEntregaDescFechaCreacionDesc(
            Long equipoId,
            Collection<OrderStatusE> estadosFinalizados
    );
}
