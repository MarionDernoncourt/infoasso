package com.infoasso.api.dto.association;

import com.infoasso.api.model.CategoryType;
import com.infoasso.api.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociationUpdateDto {

    private String officialName;
    private String displayName;

    private String description;

    @Email
    private String email;

    private String phoneNumber;

    private String website;

    private String streetAddress;
    private String zipCode;
    private String city;

      @NotBlank(message = "Le label de la catégorie (ex: Football) est obligatoire")
    private String categoryLabel;

    @NotNull(message = "Le type de catégorie (ex: SPORT) est obligatoire")
    private CategoryType categoryType;

}
