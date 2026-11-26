package com.int_solutions.sys.service.reviewService;

import com.int_solutions.sys.dto.reviewDto.ReviewCreateRequestDto;
import com.int_solutions.sys.dto.reviewDto.ReviewResponseDto;
import com.int_solutions.sys.entity.auth.RoleE;
import com.int_solutions.sys.entity.review.Review;
import com.int_solutions.sys.entity.user.Customer;
import com.int_solutions.sys.entity.user.User;
import com.int_solutions.sys.entity.workOrder.Order;
import com.int_solutions.sys.entity.workOrder.OrderStatusE;
import com.int_solutions.sys.mapper.ReviewMapper;
import com.int_solutions.sys.repository.ReviewRepository;
import com.int_solutions.sys.util.SecurityContextHelper;
import com.int_solutions.sys.util.order.OrderAuditHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImp implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final SecurityContextHelper securityContextHelper;
    private final OrderAuditHelper orderAuditHelper;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponseDto crearResena(Long ordenId, ReviewCreateRequestDto request) {
        Customer cliente = securityContextHelper.obtenerClienteEnSesion();
        Order orden = orderAuditHelper.buscarOrden(ordenId);

        if (!orden.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new AccessDeniedException("No tiene autorización para calificar esta orden.");
        }

        if (orden.getEstadoOrden().getTipoEstado() != OrderStatusE.ENTREGADO) {
            throw new IllegalStateException("La reseña se habilitará únicamente cuando el equipo haya sido entregado.");
        }

        if (reviewRepository.existsByOrdenTrabajo_OrdenId(ordenId)) {
            throw new IllegalStateException("Esta orden ya cuenta con una reseña registrada.");
        }

        Review guardada = reviewRepository.save(
                reviewMapper.toEntity(request, orden, cliente, orden.getTecnico())
        );
        return reviewMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto obtenerPorOrden(Long ordenId) {
        User usuario = securityContextHelper.obtenerUsuarioEnSesion();
        Review resena = reviewRepository.findByOrdenTrabajo_OrdenId(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("No existe reseña registrada para esta orden."));

        boolean esCliente = usuario.getRoles().stream().anyMatch(r -> r.getNombreRol() == RoleE.CLIENTE);
        if (esCliente) {
            Customer cliente = securityContextHelper.obtenerClienteEnSesion();
            if (!resena.getCliente().getClienteId().equals(cliente.getClienteId())) {
                throw new AccessDeniedException("No tiene autorización para consultar esta reseña.");
            }
        }

        return reviewMapper.toResponseDto(resena);
    }
}
