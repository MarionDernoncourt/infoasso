package com.infoasso.api.dto.association;

import com.infoasso.api.model.CategoryType;
import jakarta.persistence.Column;
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

    @NotBlank(message = "Le numéro RNA est obligatoire.")
    @Pattern(regexp = "^W\\d{9}$", message = "Le format du RNA est invalide (ex: W123456789)")
    private String rnaNumber;

    private String officialName;

    @NotBlank(message = "Le nom d'usage (ex: Club de Foot de Loos) est obligatoire.")
    private String displayName;


    @NotBlank(message = "La description doit être complétée.")
    private String description;

    @NotBlank(message = "L'email de contact est obligatoire.")
    @Email(message = "Email invalide.")
    private String email;

    @Pattern(regexp = "^(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}$",
            message = "Le numéro de téléphone n'est pas valide")
    private String phoneNumber;

    private String website;

    // --- Nouveaux champs Adresse ---
    private String streetAddress;

    private String zipCode;

    @NotBlank(message = "La ville de rattachement est obligatoire.")
    private String city;

    // --- Catégorie ---
    @NotBlank(message = "Le label de la catégorie est obligatoire.")
    private String categoryLabel;

    @NotNull(message = "Le type de catégorie est obligatoire.")
    private CategoryType categoryType;


}
