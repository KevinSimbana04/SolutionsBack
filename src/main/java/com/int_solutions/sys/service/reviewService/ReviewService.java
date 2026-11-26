package com.int_solutions.sys.service.reviewService;

import com.int_solutions.sys.dto.reviewDto.ReviewCreateRequestDto;
import com.int_solutions.sys.dto.reviewDto.ReviewResponseDto;

public interface ReviewService {
    ReviewResponseDto crearResena(Long ordenId, ReviewCreateRequestDto request);
    ReviewResponseDto obtenerPorOrden(Long ordenId);
}
