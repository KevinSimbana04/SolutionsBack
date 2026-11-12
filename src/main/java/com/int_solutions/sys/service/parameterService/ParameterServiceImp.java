package com.int_solutions.sys.service.parameterService;

import com.int_solutions.sys.dto.parameterDto.ParameterGeneralDto;
import com.int_solutions.sys.entity.config.Parameter;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.repository.ParameterRepository;
import com.int_solutions.sys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParameterServiceImp implements ParameterService {
    private final ParameterRepository parameterRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ParameterGeneralDto obtenerParametros() {
        return ParameterGeneralDto.builder()
                .iva(obtenerValorInt("IVA", 15))
                .tiempoGarantia(obtenerValorInt("TIEMPO_GARANTIA", 2))
                .tiempoMaximoEntrega(obtenerValorInt("TIEMPO_MAXIMO_ENTREGA", 5))
                .tiempoLimiteDiagnostico(obtenerValorInt("TIEMPO_LIMITE_DIAGNOSTICO", 3))
                .tiempoLimitePresupuesto(obtenerValorInt("TIEMPO_LIMITE_PRESUPUESTO", 3))
                .build();
    }

    @Override
    @Transactional
    public ParameterGeneralDto actualizarParametros(ParameterGeneralDto request, String userName) {
        User admin = userRepository.findByNombreUsuario(userName).orElse(null);

        guardarClave("IVA", String.valueOf(request.getIva()), admin);
        guardarClave("TIEMPO_GARANTIA", String.valueOf(request.getTiempoGarantia()), admin);
        guardarClave("TIEMPO_MAXIMO_ENTREGA", String.valueOf(request.getTiempoMaximoEntrega()), admin);
        guardarClave("TIEMPO_LIMITE_DIAGNOSTICO", String.valueOf(request.getTiempoLimiteDiagnostico()), admin);
        guardarClave("TIEMPO_LIMITE_PRESUPUESTO", String.valueOf(request.getTiempoLimitePresupuesto()), admin);

        log.info("Auditoría: Parámetros generales del sistema actualizados por [{}]", userName);

        ParameterGeneralDto respuesta = obtenerParametros();
        respuesta.setMensaje("Parámetros generales actualizados exitosamente");
        return respuesta;
    }

    private int obtenerValorInt(String clave, int porDefecto) {
        return parameterRepository.findByClave(clave)
                .map(p -> Integer.parseInt(p.getValor()))
                .orElse(porDefecto);
    }

    private void guardarClave(String clave, String nuevoValor, User admin) {
        Parameter parameter = parameterRepository.findByClave(clave)
                .orElseGet(() -> Parameter.builder().clave(clave).build());
        parameter.setValor(nuevoValor);
        parameter.setFechaActualizacion(LocalDateTime.now());
        parameter.setUsuario(admin);
        parameterRepository.save(parameter);
    }
}
