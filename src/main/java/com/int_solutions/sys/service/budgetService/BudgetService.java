package com.int_solutions.sys.service.budgetService;

import com.int_solutions.sys.dto.budgetDto.ApprovedSparePartDto;
import com.int_solutions.sys.dto.budgetDto.BudgetCreateRequestDto;
import com.int_solutions.sys.dto.budgetDto.BudgetRespondRequestDto;
import com.int_solutions.sys.dto.budgetDto.BudgetResponseDto;

import java.util.List;

public interface BudgetService {
    BudgetResponseDto emitirPresupuesto(Long ordenId, BudgetCreateRequestDto request);
    BudgetResponseDto consultarPresupuesto(Long ordenId);
    BudgetResponseDto responderPresupuesto(Long ordenId, BudgetRespondRequestDto request);
    List<ApprovedSparePartDto> listarRepuestosAprobados(Long ordenId);
}
