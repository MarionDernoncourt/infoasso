package com.infoasso.api.controller;

import com.infoasso.api.dto.location.LocationCreateDto;
import com.infoasso.api.dto.location.LocationReadDto;
import com.infoasso.api.dto.location.LocationUpdateDto;
import com.infoasso.api.service.ILocationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private ILocationService locationService;

    public LocationController(ILocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("")
    public ResponseEntity<List<LocationReadDto>> findAllLocation() {
        logger.info("GET / / Request received for all Locations");
                List<LocationReadDto> locations = locationService.findAllLocation();
                logger.info("GET / : Response 200 OK : Number of Location : {}", locations.size());
                return ResponseEntity.status(HttpStatus.OK).body(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationReadDto> getLocationById(@PathVariable Long id) {
        logger.info("GET / / Request received for Location with id {}", id);
        LocationReadDto location = locationService.findById(id);
        logger.info("GET / : Response 200 OK : Location : {}", location);
        return ResponseEntity.status(HttpStatus.OK).body(location);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<LocationReadDto> createLocation(@Valid @RequestBody LocationCreateDto dto) {
        logger.info("POST / : Request received to create Location : {}", dto.name());
        LocationReadDto newLocation = locationService.findOrCreateLocation(dto);
        logger.info("POST / : Response 201 CREATED : Location : {}", newLocation);
        return ResponseEntity.status(HttpStatus.CREATED).body(newLocation);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<LocationReadDto> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationUpdateDto dto) {
        logger.info("PUT / : Request received to update Location with id {}", id);
        LocationReadDto updatedLocation = locationService.updateLocation(id, dto);
        logger.info("PUT / : Response 200 OK : Location : {}", updatedLocation);
        return ResponseEntity.status(HttpStatus.OK).body(updatedLocation);
    }




}
