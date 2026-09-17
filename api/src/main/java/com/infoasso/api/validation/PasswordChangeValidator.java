package com.infoasso.api.validation;

import com.infoasso.api.dto.user.UserUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class PasswordChangeValidator implements ConstraintValidator<ValidPasswordChange, UserUpdateDto> {

    @Override
    public boolean isValid(UserUpdateDto dto, ConstraintValidatorContext context) {
        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            return true;
        }

        boolean isValid = dto.getOldPassword() != null && !dto.getOldPassword().isBlank();

        if (!isValid) {
            // Désactive le message d'erreur par défaut global
            context.disableDefaultConstraintViolation();

            // Force l'erreur à s'attacher spécifiquement au champ "oldPassword"
            context.buildConstraintViolationWithTemplate("L'ancien mot de passe est requis pour définir un nouveau mot de passe.")
                    .addPropertyNode("oldPassword")
                    .addConstraintViolation();
        }

        return isValid;
    }
}