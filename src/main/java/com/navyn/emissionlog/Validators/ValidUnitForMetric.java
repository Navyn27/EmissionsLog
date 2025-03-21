package com.navyn.emissionlog.Validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UnitForMetricValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUnitForMetric {
    String message() default "Invalid unit for the given metric type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}