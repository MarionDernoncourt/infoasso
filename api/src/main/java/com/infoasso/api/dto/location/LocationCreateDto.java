package com.infoasso.api.dto.location;

import jakarta.validation.constraints.NotBlank;

public record LocationCreateDto(
        String name,
        String address,
        @NotBlank(message = "Le nom de la ville est obligatoire.") String city,
        String zipCode) {
}
