package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.workOrder.OrderStatus;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    boolean existsByTipoEstado(OrderStatusE tipoEstado);
    Optional<OrderStatus> findByTipoEstado(OrderStatusE tipoEstado);
}
