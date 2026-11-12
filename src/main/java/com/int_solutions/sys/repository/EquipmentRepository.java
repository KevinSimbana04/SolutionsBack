package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.equipment.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    boolean existsByNumeroSerieIgnoreCase(String numeroSerie);
    List<Equipment> findByCliente_ClienteId(Long clienteId);
}
