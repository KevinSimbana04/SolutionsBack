package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.diagnosis.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findByOrdenTrabajo_OrdenId(Long ordenId);
    boolean existsByOrdenTrabajo_OrdenId(Long ordenId);
}
