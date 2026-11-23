package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.repair.Repair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairRepository extends JpaRepository<Repair , Long> {
    List<Repair> findByOrdenTrabajo_OrdenIdOrderByFechaInicioAsc(Long ordenId);
}
