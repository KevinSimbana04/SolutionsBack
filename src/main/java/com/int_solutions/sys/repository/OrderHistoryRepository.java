package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.workOrder.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findByOrdenOrdenIdOrderByFechaModificacionAsc(Long ordenOrdenId);

}
