package com.infoasso.api.service;

import com.infoasso.api.dto.location.LocationCreateDto;
import com.infoasso.api.dto.location.LocationReadDto;
import com.infoasso.api.dto.location.LocationUpdateDto;
import com.infoasso.api.exceptions.ResourceAlreadyExistsException;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.Location;
import com.infoasso.api.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LocationServiceIT {

    @Autowired
    private ILocationService locationService;
    @Autowired
    private LocationRepository locationRepository;

    private Location location;

    @BeforeEach
    public void setUp() {
        location = new Location();
        location.setName("test");
        location.setAddress("1 square street");
        location.setCity("London");


        locationRepository.save(location);
    }

    @Test
    public void findAll_WhenSuccess() {
        List<LocationReadDto> locations = locationService.findAllLocation();
        assertEquals(1, locations.size());
    }

    @Test
    public void findById_WhenSuccess() {
        Long id = location.getId();
        LocationReadDto loc = locationService.findById(id);

        assertEquals("test", loc.name());
    }

    @Test
    public void findById_WhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> locationService.findById(45L));
    }

    @Test
    public void create_WhenSuccess() {

        LocationCreateDto newLocDto = new LocationCreateDto("Stade", "grand place", "Lille", "59000");
                LocationReadDto createdLoc = locationService.findOrCreateLocation(newLocDto);
        List<LocationReadDto> listLocations = locationService.findAllLocation();

        assertEquals(2, listLocations.size());
        assertEquals(newLocDto.name(), createdLoc.name());
    }

 @Test
    public void update_WhenSuccess() {
        Long id = location.getId();
     LocationUpdateDto dtoToUpdate = new LocationUpdateDto("", "", "Manchester", "");

     LocationReadDto updatedLoc = locationService.updateLocation(id, dtoToUpdate);

     assertEquals("Manchester", updatedLoc.city());
 }

}
