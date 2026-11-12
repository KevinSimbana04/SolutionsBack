package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.equipment.EquipmentType;
import com.int_solutions.sys.entity.equipment.TypeE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long> {
    boolean existsByNombreEquipo(TypeE nombreEquipo);
    Optional<EquipmentType> findByNombreEquipo(TypeE nombreEquipo);
}
