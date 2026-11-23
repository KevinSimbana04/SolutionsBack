package com.int_solutions.sys.util;

import com.int_solutions.sys.entity.workOrder.OrderStatus;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import com.int_solutions.sys.repository.OrderStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(5)
@RequiredArgsConstructor
@Slf4j
public class StatusOrderSeeder implements CommandLineRunner {
    private final OrderStatusRepository orderStatusRepository;

    @Override
    @Transactional
    public  void run(String... args){
        for(OrderStatusE eo: OrderStatusE.values()){
            if(!orderStatusRepository.existsByTipoEstado(eo)){
                orderStatusRepository.save(OrderStatus.builder()
                        .tipoEstado(eo)
                        .build());
            }
        }
        log.info("Estados de la orden generada");
    }

}
