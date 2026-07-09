package com.infoasso.api.service.impl;

import com.infoasso.api.dto.location.LocationCreateDto;
import com.infoasso.api.dto.location.LocationReadDto;
import com.infoasso.api.dto.location.LocationUpdateDto;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.Location;
import com.infoasso.api.repository.LocationRepository;
import com.infoasso.api.service.ILocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements ILocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);

    private LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<LocationReadDto> findAllLocation() {
        logger.info("Trying to find all location");
        List<Location> locations = locationRepository.findAll();
        logger.info("Locations found: " + locations.size());
        return locations.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public LocationReadDto findById(Long id) {
        logger.info("Trying to find the location with id: {}", id);
        Location location = locationRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Location", id));
        logger.info("Found the location with id: " + id);
        return mapToDto(location);
    }

    @Override
    public LocationReadDto findOrCreateLocation(LocationCreateDto dto) {
        logger.info("Trying to create a location");

        String name = (dto.name() != null) ? dto.name().trim() : "";
        String address = (dto.address() != null) ? dto.address().trim() : "";
        String city = (dto.city().trim());

        Location location = locationRepository.findByNameIgnoreCaseAndAddressIgnoreCaseAndCityIgnoreCase(name, address, city).orElseGet(() -> {
            Location newLoc = mapToEntity(dto);
            return locationRepository.save(newLoc);
        });

        logger.info("Location processed: name={}, id={}", location.getName(), location.getId());
        return mapToDto(location);
    }

    @Override
    public Location findOrCreateEntity(Location dto) {
        String name = (dto.getName() != null) ? dto.getName().trim() : "";
        String address = (dto.getAddress() != null) ? dto.getAddress().trim() : "";
        String city = (dto.getCity().trim());

        return locationRepository.findByNameIgnoreCaseAndAddressIgnoreCaseAndCityIgnoreCase(name, address, city)
                .orElseGet(() -> locationRepository.save(dto));
    }


    @Override
    public LocationReadDto updateLocation(Long id, LocationUpdateDto updateDto) {
        logger.info("Trying to update a location with id: {}", id);

        // Verif si la Location existe
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));

        // Modification de la Location
        updateEntityFromDto(location, updateDto);
        // Sauvegarde de l'objet modifié
        Location updatedLocation = locationRepository.save(location);

        logger.info("Location with id {} updated with succes !", id);
        return mapToDto(updatedLocation);
    }


    private Location mapToEntity(LocationCreateDto dto) {
        Location location = new Location();

        location.setName(dto.name());
        location.setAddress(dto.address());
        location.setCity(dto.city());
        location.setZipCode(dto.zipCode());

        return location;
    }

    private LocationReadDto mapToDto(Location location) {
        return new LocationReadDto(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getCity(),
                location.getZipCode()
        );
    }

    private Location updateEntityFromDto(Location location, LocationUpdateDto dto) {
        if (dto.name() != null) {
            location.setName(dto.name());
        }
        if (dto.address() != null) {
            location.setAddress(dto.address());
        }
        if (dto.city() != null) {
            location.setCity(dto.city());
        }
        if (dto.zipCode() != null) {
            location.setZipCode(dto.zipCode());
        }

        return location;
    }
}
