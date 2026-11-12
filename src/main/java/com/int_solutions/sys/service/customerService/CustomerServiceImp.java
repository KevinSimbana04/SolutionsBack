package com.int_solutions.sys.service.customerService;

import com.int_solutions.sys.dto.userDto.IdentityUserDto;
import com.int_solutions.sys.dto.userDto.customerDto.CustomerRequestDto;
import com.int_solutions.sys.dto.userDto.customerDto.CustomerResponseDto;
import com.int_solutions.sys.entity.auth.RoleE;
import com.int_solutions.sys.entity.user.Customer;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.mapper.CustomerMapper;
import com.int_solutions.sys.repository.ActivateTokenRepository;
import com.int_solutions.sys.repository.CustomerRepository;
import com.int_solutions.sys.repository.RoleRepository;
import com.int_solutions.sys.repository.UserRepository;
import com.int_solutions.sys.service.userService.EmailService;
import com.int_solutions.sys.service.userService.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImp implements CustomerService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final CustomerMapper customerMapper;
    private final UserService userService;

    @Override
    @Transactional
    public CustomerResponseDto crearCliente(CustomerRequestDto request) {

        IdentityUserDto identidad = userService.registrarRolUsuario(
                request.getIdentificacion(), request.getCorreo(), RoleE.CLIENTE
        );

        Customer customer= Customer.builder()
                .identificacion(request.getIdentificacion())
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .usuario(identidad.usuario())
                .build();
        Customer saveCustomer= customerRepository.save(customer);

        if (identidad.esNuevoUsuario()) {
            String nombreCompleto = saveCustomer.getNombres() + " " + saveCustomer.getApellidos();
            emailService.enviarActivacion(
                    identidad.usuario().getCorreo(),
                    nombreCompleto,
                    identidad.usuario().getNombreUsuario(),
                    identidad.claveTemporal(),
                    identidad.tokenActivacion(),
                    "cliente"
            );
            return customerMapper.toResponseDto(saveCustomer, "Cliente registrado exitosamente. Correo enviado.");
        }
        return customerMapper.toResponseDto(saveCustomer, "Rol de Cliente añadido.");

    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> listarClientes(String buscar) {
        List<Customer> lista = (buscar != null && !buscar.trim().isEmpty())
                ? customerRepository.buscarPorCedulaONombre(buscar.trim())
                : customerRepository.findAll();
        return customerMapper.toResponseDtoList(lista);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto obtenerPorId(Long id) {
        Customer cliente = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
        return customerMapper.toResponseDtoSinMensaje(cliente);
    }

    @Override
    @Transactional
    public CustomerResponseDto actualizarCliente(Long id, CustomerRequestDto request) {
        Customer cliente = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
        User usuario = cliente.getUsuario();

        if (!cliente.getIdentificacion().equals(request.getIdentificacion()) &&
                customerRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new IllegalArgumentException("La cédula ingresada ya pertenece a otro cliente");
        }
        if (!cliente.getTelefono().equals(request.getTelefono()) &&
                customerRepository.existsByTelefono(request.getTelefono())) {
            throw new IllegalArgumentException("El teléfono ya pertenece a otro cliente");
        }
        if (!usuario.getCorreo().equalsIgnoreCase(request.getCorreo()) &&
                userRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("El correo electrónico ya pertenece a otro cliente");
        }

        cliente.setIdentificacion(request.getIdentificacion());
        cliente.setNombres(request.getNombres());
        cliente.setApellidos(request.getApellidos());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());

        usuario.setCorreo(request.getCorreo());
        usuario.setNombreUsuario(request.getIdentificacion());

        userRepository.save(usuario);
        Customer clienteActualizado = customerRepository.save(cliente);

        log.info("Auditoría: Cliente ID {} actualizado correctamente", id);

        return customerMapper.toResponseDto(clienteActualizado, "Datos del cliente actualizados correctamente");
    }

    @Override
    @Transactional
    public void desactivarCliente(Long id, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe confirmar el motivo para desactivar la cuenta del cliente");
        }
        Customer cliente = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));

        User usuario = cliente.getUsuario();
        usuario.setEstadoCuenta(false); // Borrado lógico
        usuario.setMotivo(motivo.trim());
        userRepository.save(usuario);

        log.info("Auditoría: Cliente ID {} desactivado lógicamente. Motivo: {}", id, motivo);
    }
}
