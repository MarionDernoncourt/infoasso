package com.infoasso.api.service;

import com.infoasso.api.dto.location.LocationCreateDto;
import com.infoasso.api.dto.location.LocationReadDto;
import com.infoasso.api.dto.location.LocationUpdateDto;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.Location;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ILocationService {
    List<LocationReadDto> findAllLocation();

    Location findOrCreateLocation(Association association, LocationCreateDto dto);

    List<LocationReadDto> findAllByAssociationId(Long associationId);

    LocationReadDto updateLocation(Long id, Long associationId, LocationUpdateDto updateDto, String userEmail);

    LocationReadDto findByIdAndAssociationId(Long associationId, Long locationId);

    LocationReadDto createLocation(Long associationId, @Valid LocationCreateDto createDto, String userEmail);
}
