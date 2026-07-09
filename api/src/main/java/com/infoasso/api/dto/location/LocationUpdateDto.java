package com.infoasso.api.dto.location;

import jakarta.validation.constraints.NotBlank;

public record LocationUpdateDto(
        String name,
        String address,
        String city,
        String zipCode
) {
}
