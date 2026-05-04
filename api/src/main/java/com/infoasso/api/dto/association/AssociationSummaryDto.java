package com.infoasso.api.dto.association;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociationSummaryDto {

    @NotNull
    private Long id;

    @NotBlank
    private String displayName;

    private String categoryLabel;
}
