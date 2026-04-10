package com.infoasso.api.dto.association;

import com.infoasso.api.model.CategoryType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociationUpdateDto {

    private String name;

    private String description;

    @Email
    private String email;

    private String phoneNumber;

    private String website;


    @NotBlank(message = "Le label de la catégorie (ex: Football) est obligatoire")
    private String categoryLabel;

    @NotNull(message = "Le type de catégorie (ex: SPORT) est obligatoire")
    private CategoryType categoryType;
    private Long ownerId;

}
