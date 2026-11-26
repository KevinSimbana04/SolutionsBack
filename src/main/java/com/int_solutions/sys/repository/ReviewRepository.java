package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByOrdenTrabajo_OrdenId(Long ordenId);

    Optional<Review> findByOrdenTrabajo_OrdenId(Long ordenId);

    List<Review> findByTecnico_EmpleadoIdOrderByFechaCreacionDesc(Long tecnicoId);
}