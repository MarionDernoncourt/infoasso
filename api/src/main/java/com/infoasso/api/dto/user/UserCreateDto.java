package com.infoasso.api.dto.user;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {

    @Email(message = "Le format de l'email est invalide.")
    @NotBlank(message = "L'email est obligatoire.")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule et un chiffre."
    )
    private String password;

    @AssertTrue(message = "Vous devez accepter le traitement de vos données (RGPD) pour vous inscrire.")
    private boolean gdprConsent;
}
