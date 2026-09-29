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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/associations/{associationId}/locations")
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private final ILocationService locationService;

    public LocationController(ILocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationReadDto>> findAllByAssociation(@PathVariable Long associationId) {
        logger.info("REST request to get all locations for association id : {}", associationId);
        List<LocationReadDto> locations = locationService.findAllByAssociationId(associationId);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<LocationReadDto> findById(
            @PathVariable Long associationId,
            @PathVariable Long locationId) {
        logger.info("REST request to get location id : {} for association id : {}", locationId, associationId);
        LocationReadDto location = locationService.findByIdAndAssociationId(associationId, locationId);
        return ResponseEntity.ok(location);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<LocationReadDto> createLocation(
            @PathVariable Long associationId,
            @Valid @RequestBody LocationCreateDto createDto,
            Principal principal) {
        logger.info("REST request to create a location for association id : {}", associationId);
        String userEmail = principal.getName();
        LocationReadDto createdLocation = locationService.createLocation(associationId, createDto, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation);
    }

    @PutMapping("/{locationId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<LocationReadDto> update(
            @PathVariable Long associationId,
            @PathVariable Long locationId,
            @Valid @RequestBody LocationUpdateDto updateDto,
            Principal principal) {
        logger.info("REST request to update location id : {} for association id : {}", locationId, associationId);
        String userEmail = principal.getName();
        LocationReadDto updatedLocation = locationService.updateLocation(associationId, locationId, updateDto, userEmail);
        return ResponseEntity.ok(updatedLocation);
    }




}
