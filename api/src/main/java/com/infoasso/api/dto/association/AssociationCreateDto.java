package com.infoasso.api.dto.association;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociationCreateDto {

    @NotBlank(message = "Le nom ne peut pas être vide.")
    private String name;

    @NotBlank(message = "La description doit être complétée.")
    private String description;

    @NotBlank(message = "L'email est obligatoire.")
    @Email
    private String email;

    @Pattern(regexp = "^(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}$",
            message = "Le numéro de téléphone n'est pas valide")
    private String phoneNumber;

    private String website;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categoryId;


    private Long userId;
}
