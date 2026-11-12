package com.int_solutions.sys.service.equipmentService;

import com.int_solutions.sys.dto.equipmentDto.EquipmentRequestDto;
import com.int_solutions.sys.dto.equipmentDto.EquipmentResponseDto;
import com.int_solutions.sys.entity.equipment.Equipment;
import com.int_solutions.sys.entity.equipment.EquipmentType;
import com.int_solutions.sys.entity.user.Customer;
import com.int_solutions.sys.mapper.EquipmentMapper;
import com.int_solutions.sys.repository.CustomerRepository;
import com.int_solutions.sys.repository.EquipmentRepository;
import com.int_solutions.sys.repository.EquipmentTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentServiceImp implements EquipmentService {
    private final CustomerRepository customerRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    @Override
    @Transactional
    public EquipmentResponseDto registrarEquipo(EquipmentRequestDto request) {
        if (equipmentRepository.existsByNumeroSerieIgnoreCase(request.getNumeroSerie())) {
            throw new IllegalArgumentException("Ya existe ese equipo con el numero de serie: " + request.getNumeroSerie());
        }

        Customer cliente = customerRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente seleccionado no existe"));

        EquipmentType tipoEquipo = equipmentTypeRepository.findByNombreEquipo(request.getTipoEquipo())
                .orElseThrow(() -> new IllegalArgumentException("Categoría de equipo no encontrada: " + request.getTipoEquipo()));

        Equipment nuevoEquipo = Equipment.builder()
                .cliente(cliente)
                .tipoEquipo(tipoEquipo)
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .numeroSerie(request.getNumeroSerie())
                .color(request.getColor())
                .accesorios(request.getAccesorios())
                .estadoFisicoInicial(request.getEstadoFisicoInicial())
                .fotografiaInicial(request.getFotografiaInicial())
                .fallaReportada(request.getFallaReportada())
                .observaciones(request.getObservaciones())
                .build();

        Equipment equipoGuardado = equipmentRepository.save(nuevoEquipo);
        log.info("Auditoría: Equipo registrado exitosamente [ID={}, Serie={}]", equipoGuardado.getEquipoId(), equipoGuardado.getNumeroSerie());
        return equipmentMapper.toResponseDto(equipoGuardado, "Equipo registrado exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentResponseDto> listarEquipos(Long clienteId) {
        List<Equipment> equipos = (clienteId != null)
                ? equipmentRepository.findByCliente_ClienteId(clienteId)
                : equipmentRepository.findAll();
        return equipmentMapper.toResponseDtoList(equipos);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentResponseDto obtenerPorId(Long id) {
        Equipment equipo = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado con ID: " + id));
        return equipmentMapper.toResponseDtoSinMensaje(equipo);
    }

    @Override
    @Transactional
    public EquipmentResponseDto actualizarEquipo(Long id, EquipmentRequestDto request) {
        Equipment equipo = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipo no encontrado con ID: " + id));

        if (!equipo.getNumeroSerie().equalsIgnoreCase(request.getNumeroSerie()) &&
                equipmentRepository.existsByNumeroSerieIgnoreCase(request.getNumeroSerie())) {
            throw new IllegalArgumentException("Ya existe otro equipo con el número de serie: " + request.getNumeroSerie());
        }

        EquipmentType tipo = equipmentTypeRepository.findByNombreEquipo(request.getTipoEquipo())
                .orElseThrow(() -> new IllegalArgumentException("Categoría de equipo no admitida: " + request.getTipoEquipo()));

        equipo.setTipoEquipo(tipo);
        equipo.setMarca(request.getMarca());
        equipo.setModelo(request.getModelo());
        equipo.setNumeroSerie(request.getNumeroSerie());
        equipo.setColor(request.getColor());
        equipo.setAccesorios(request.getAccesorios());
        equipo.setEstadoFisicoInicial(request.getEstadoFisicoInicial());
        equipo.setFallaReportada(request.getFallaReportada());
        equipo.setObservaciones(request.getObservaciones());

        Equipment actualizado = equipmentRepository.save(equipo);
        log.info("Auditoría: Equipo ID {} actualizado correctamente", id);
        return equipmentMapper.toResponseDto(actualizado, "Datos del equipo actualizados correctamente");
    }
}
