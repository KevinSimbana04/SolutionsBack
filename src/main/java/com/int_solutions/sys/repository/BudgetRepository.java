package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.budget.Budget;
import com.int_solutions.sys.entity.workOrder.Order;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByOrdenTrabajo_OrdenId(Long ordenId);

    @EntityGraph(attributePaths = {
            "ordenTrabajo",
            "ordenTrabajo.cliente",
            "ordenTrabajo.equipo",
            "ordenTrabajo.estadoOrden"
    })
    List<Budget> findByEstadoPresupuesto(String estadoPresupuesto);
}
