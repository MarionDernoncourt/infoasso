package com.infoasso.api.dto.location;

import jakarta.validation.constraints.NotBlank;

public record LocationReadDto(

        Long id,
        String name,
        String address,
        @NotBlank(message = "Le nom de la ville est obligatoire.") String city,
        String zipCode) {
}
