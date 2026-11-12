package com.int_solutions.sys.util;

import com.int_solutions.sys.entity.config.Parameter;
import com.int_solutions.sys.repository.ParameterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class ParameterSeeder implements CommandLineRunner {
    private final ParameterRepository parameterRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, String> parametrosDefecto = Map.of(
                "IVA", "15",
                "TIEMPO_GARANTIA", "2",
                "TIEMPO_MAXIMO_ENTREGA", "5",
                "TIEMPO_LIMITE_DIAGNOSTICO", "3",
                "TIEMPO_LIMITE_PRESUPUESTO", "3"
        );

        parametrosDefecto.forEach((clave, valor) -> {
            if (!parameterRepository.existsByClave(clave)) {
                parameterRepository.save(Parameter.builder()
                        .clave(clave)
                        .valor(valor)
                        .fechaActualizacion(LocalDateTime.now())
                        .descripcion("Parámetro configurado por defecto")
                        .build());
            }
        });
        log.info("Parámetros generales del sistema inicializados correctamente");
    }
}
