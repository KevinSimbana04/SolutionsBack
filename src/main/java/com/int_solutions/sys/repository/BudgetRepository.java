package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.budget.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByOrdenTrabajo_OrdenId(Long ordenId);
}
