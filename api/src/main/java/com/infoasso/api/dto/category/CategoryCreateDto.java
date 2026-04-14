package com.infoasso.api.dto.category;

import com.infoasso.api.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateDto {

    @NotBlank
    private String label;

    @NotNull
    private CategoryType type;
}
