package com.int_solutions.sys.repository;

import com.int_solutions.sys.entity.user.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByIdentificacion(String identificacion);
    boolean existsByTelefono(String telefono);
    Optional<Customer> findByIdentificacion(String identificacion);

    @Query("SELECT c FROM Customer c WHERE c.identificacion LIKE %:filtro% " +
            "OR LOWER(c.nombres) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
            "OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    List<Customer> buscarPorCedulaONombre(@Param("filtro") String filtro);
}
