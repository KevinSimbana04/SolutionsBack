package com.int_solutions.sys.util;

import com.int_solutions.sys.entity.equipment.EquipmentType;
import com.int_solutions.sys.entity.equipment.TypeE;
import com.int_solutions.sys.repository.EquipmentRepository;
import com.int_solutions.sys.repository.EquipmentTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
@RequiredArgsConstructor
@Slf4j
public class EquipmetTypeSeeder implements CommandLineRunner {
    private final EquipmentTypeRepository equipmentTypeRepository;
    @Override
    @Transactional
    public void run(String... args){
        for(TypeE te: TypeE.values()){
            if(!equipmentTypeRepository.existsByNombreEquipo(te)){
                equipmentTypeRepository.save(EquipmentType.builder()
                        .nombreEquipo(te)
                        .build());
            }
        }
        log.info("Tipos de equipo inicializados");
    }
}
