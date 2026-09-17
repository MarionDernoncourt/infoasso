package com.infoasso.api.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordChangeValidator.class)
@Documented
public @interface ValidPasswordChange {
    String message() default "L'ancien mot de passe est requis pour définir un nouveau mot de passe.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}