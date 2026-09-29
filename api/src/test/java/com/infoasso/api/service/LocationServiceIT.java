package com.infoasso.api.service;

import com.infoasso.api.dto.location.LocationCreateDto;
import com.infoasso.api.dto.location.LocationReadDto;
import com.infoasso.api.dto.location.LocationUpdateDto;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.Category;
import com.infoasso.api.model.CategoryType;
import com.infoasso.api.model.Location;
import com.infoasso.api.model.Role;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.AssociationRepository;
import com.infoasso.api.repository.CategoryRepository;
import com.infoasso.api.repository.LocationRepository;
import com.infoasso.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LocationServiceIT {

    @Autowired
    private ILocationService locationService;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AssociationRepository associationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Location location;
    private Association association;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("user@test.fr");
        user.setPassword("Password123");
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        Category category = new Category();
        category.setLabel("Sport");
        category.setType(CategoryType.SPORT);
        categoryRepository.save(category);

        association = new Association();
        association.setRnaNumber("W123456789");
        association.setOfficialName("Test Association");
        association.setDisplayName("Test Asso");
        association.setDescription("Description");
        association.setEmail("asso@test.fr");
        association.setCategory(category);
        association.setOwner(user);
        associationRepository.save(association);

        location = new Location();
        location.setName("test");
        location.setAddress("1 square street");
        location.setCity("London");
        location.setZipCode("SW1A");
        location.setAssociation(association);
        location = locationRepository.save(location);
    }

    @Test
    public void findAll_WhenSuccess() {
        List<LocationReadDto> locations = locationService.findAllLocation();
        assertEquals(1, locations.size());
    }

    @Test
    public void findById_WhenSuccess() {
        Long id = location.getId();
        LocationReadDto loc = locationService.findByIdAndAssociationId(association.getId(), id);

        assertEquals("test", loc.name());
    }

    @Test
    public void findById_WhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> locationService.findByIdAndAssociationId(association.getId(), 45L));
    }

    @Test
    public void findOrCreate_WhenSuccess() {
        LocationCreateDto newLocDto = new LocationCreateDto("Stade", "grand place", "Lille", "59000");

        // findOrCreateLocation retourne l'entité Location
        Location createdLoc = locationService.findOrCreateLocation(association, newLocDto );
        List<LocationReadDto> listLocations = locationService.findAllLocation();

        assertEquals(2, listLocations.size());
        assertEquals(newLocDto.name(), createdLoc.getName());
    }

    @Test
    @WithMockUser(username = "user@test.fr")
    public void update_WhenSuccess() {
        Long id = location.getId();
        LocationUpdateDto dtoToUpdate = new LocationUpdateDto("", "", "Manchester", "");

        // Utilisation de la signature avec associationId et userEmail si requis par ton service
        LocationReadDto updatedLoc = locationService.updateLocation(id, association.getId(), dtoToUpdate, "user@test.fr");

        assertEquals("Manchester", updatedLoc.city());
    }
}