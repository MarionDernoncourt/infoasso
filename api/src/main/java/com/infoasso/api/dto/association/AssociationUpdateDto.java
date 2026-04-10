package com.infoasso.api.dto.association;

import jakarta.validation.constraints.Email;
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


    private Long categoryId;

    private Long ownerId;

}
