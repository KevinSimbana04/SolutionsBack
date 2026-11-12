package com.int_solutions.sys.util.validationI;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidationI.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdentificacionE {
    String message() default "La cédula ingresada no es válida o no existe";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
