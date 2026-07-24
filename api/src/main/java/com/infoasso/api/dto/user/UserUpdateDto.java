package com.infoasso.api.dto.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    @Email(message = "Le format de l'email est invalide.")
    private String email;

    private String oldPassword;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule et un chiffre."
    )
    private String newPassword;
}
