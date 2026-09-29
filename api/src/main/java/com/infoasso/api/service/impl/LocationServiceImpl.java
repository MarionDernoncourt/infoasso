package com.infoasso.api.service.impl;

import com.infoasso.api.dto.location.LocationCreateDto;
import com.infoasso.api.dto.location.LocationReadDto;
import com.infoasso.api.dto.location.LocationUpdateDto;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.Location;
import com.infoasso.api.model.Schedule;
import com.infoasso.api.repository.AssociationRepository;
import com.infoasso.api.repository.LocationRepository;
import com.infoasso.api.repository.ScheduleRepository;
import com.infoasso.api.service.ILocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements ILocationService {

    private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);

    private final LocationRepository locationRepository;
    private final ScheduleRepository scheduleRepository;
    private final AssociationRepository associationRepository;

    public LocationServiceImpl(LocationRepository locationRepository, ScheduleRepository scheduleRepository, AssociationRepository associationRepository) {
        this.locationRepository = locationRepository;
        this.scheduleRepository = scheduleRepository;
        this.associationRepository = associationRepository;
        }

    @Override
    public List<LocationReadDto> findAllLocation() {
        logger.info("Trying to find all locations");
        List<Location> locations = locationRepository.findAll();
        return locations.stream()
                .map(this::mapToDto)
                .toList();
    }
    @Override

    public List<LocationReadDto> findAllByAssociationId(Long associationId) {
        logger.info("Trying to find all locations for association id: {}", associationId);

        // Récupère toutes les locations uniques associées aux plannings de cette association
        List<Location> locations = scheduleRepository.findByAssociationId(associationId).stream()
                .map(Schedule::getLocation)
                .filter(loc -> loc != null)
                .distinct()
                .toList();

        return locations.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public LocationReadDto findByIdAndAssociationId(Long associationId, Long locationId) {
        logger.info("Trying to find location with id: {} for association id: {}", locationId, associationId);
        // Récupère la location par son id
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location", locationId));
        // Vérifie si cette location est associée à l'association donnée via les plannings
        boolean isAssociated = scheduleRepository.existsByAssociationIdAndLocationId(associationId, locationId);
        if (!isAssociated) {
            logger.warn("Location with id: {} is not associated with association id: {}", locationId, associationId);
            throw new ResourceNotFoundException("Location", locationId);
        }
        return mapToDto(location);
    }

    @Override
    public Location findOrCreateLocation(Association association, LocationCreateDto dto) {
        logger.info("Processing location creation or lookup for name: {}, city: {}", dto.name(), dto.city());

        String name = (dto.name() != null) ? dto.name().trim() : "";
        String address = (dto.address() != null) ? dto.address().trim() : "";
        String city = (dto.city() != null) ? dto.city().trim() : "";

        // On cherche si elle existe déjà en base (insensible à la casse)
        return locationRepository.findByNameIgnoreCaseAndAddressIgnoreCaseAndCityIgnoreCase(name, address, city)
                .orElseGet(() -> {
                    logger.info("Location does not exist, creating a new one.");
                    Location newLoc = new Location();
                    newLoc.setName(name);
                    newLoc.setAddress(address);
                    newLoc.setCity(city);
                    newLoc.setZipCode(dto.zipCode());
                    newLoc.setAssociation(association); // On lie la location à l'association


                    return locationRepository.save(newLoc); // On sauvegarde et on retourne l'entité JPA
                });
    }

    @Override
    public LocationReadDto createLocation(Long associationId, LocationCreateDto createDto, String userEmail) {
        logger.info("Creating or linking location for association id : {}", associationId);

        // 1. Vérification optionnelle de sécurité (propriétaire de l'association)
        Association association = associationRepository.findById(associationId)
                .orElseThrow(() -> new ResourceNotFoundException("Association", associationId));

        if (!association.getOwner().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Tu n'es pas autorisé à ajouter une location pour cette association.");
        }

        // 2. On réutilise la méthode findOrCreateLocation !
        Location locationEntity = findOrCreateLocation(association, createDto);

        // 3. On retourne le DTO pour le contrôleur
        return mapToDto(locationEntity);
    }



    @Override
    public LocationReadDto updateLocation(Long id, Long associationId, LocationUpdateDto updateDto, String userEmail) {
        logger.info("Trying to update a location with id: {}", id);
// Petite sécurité anti-IDOR à ajouter pour la V2 : vérifier que la location appartient bien à l'association
        // et que le user est bien le propriétaire de l'associati
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));

        updateEntityFromDto(location, updateDto);
        Location updatedLocation = locationRepository.save(location);

        logger.info("Location with id {} updated successfully !", id);
        return mapToDto(updatedLocation);
    }




    private LocationReadDto mapToDto(Location location) {
        return new LocationReadDto(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getCity(),
                location.getZipCode(),
                location.getAssociation().getId()
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
