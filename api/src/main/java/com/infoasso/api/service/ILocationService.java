package com.infoasso.api.service;

import com.infoasso.api.dto.location.LocationCreateDto;
import com.infoasso.api.dto.location.LocationReadDto;
import com.infoasso.api.dto.location.LocationUpdateDto;
import com.infoasso.api.model.Location;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ILocationService {
    List<LocationReadDto> findAllLocation();

    LocationReadDto findById(Long id);

    LocationReadDto findOrCreateLocation(LocationCreateDto dto);

    Location findOrCreateEntity(Location dto);

    LocationReadDto updateLocation(Long id, LocationUpdateDto updateDto);
}
