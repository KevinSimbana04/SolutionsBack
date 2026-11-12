package com.int_solutions.sys.dto.userDto.employeeDto;

import com.int_solutions.sys.dto.userDto.PersonRequestDto;
import com.int_solutions.sys.entity.auth.RoleE;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDto extends PersonRequestDto {

    @NotNull(message = "El rol es obligatorio")
    private RoleE rol;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargo;
}
