package com.infoasso.api.dto.association;

import com.infoasso.api.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociationReadDto {
    private Long id;

    private String rnaNumber;

    private String officialName;

    private String displayName;

    private String description;

    private String streetAddress;
    private String zipCode;
    private String city;

    private String email;

    private String phoneNumber;

    private String website;

    private String categoryLabel;

    private CategoryType categoryType;

    private String ownerEmail;

}
