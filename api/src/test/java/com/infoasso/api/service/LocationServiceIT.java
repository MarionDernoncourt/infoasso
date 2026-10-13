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
import org.springframework.security.access.AccessDeniedException;
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
    public void findByIdAndAssociationId_WhenSuccess() {
        LocationReadDto found = locationService.findByIdAndAssociationId(location.getId(), association.getId());
        assertNotNull(found);
        assertEquals("London", found.city());
    }

    @Test
    public void findById_WhenNotFound() {
        // Test avec un ID de lieu inexistant (999L) pour la bonne association
        assertThrows(ResourceNotFoundException.class, () -> locationService.findByIdAndAssociationId(999L, association.getId()));
    }

    @Test
    public void findOrCreate_WhenSuccess() {
        LocationCreateDto newLocDto = new LocationCreateDto("Stade", "grand place", "Lille", "59000");

        Location createdLoc = locationService.findOrCreateLocation(association, newLocDto);
        List<LocationReadDto> listLocations = locationService.findAllLocation();

        assertEquals(2, listLocations.size());
        assertEquals(newLocDto.name(), createdLoc.getName());
    }

    @Test
    public void createLocation_WhenSuccess() {
        LocationCreateDto createDto = new LocationCreateDto("Salle Polyvalente", "Rue de la Paix", "Paris", "75001");
        LocationReadDto created = locationService.createLocation(association.getId(), createDto, "user@test.fr");

        assertNotNull(created);
        assertEquals("Paris", created.city());
    }

    @Test
    public void createLocation_WhenAccessDenied() {
        // Un utilisateur non propriétaire essaie d'ajouter un lieu
        LocationCreateDto createDto = new LocationCreateDto("Salle Pirate", "Rue du Port", "Marseille", "13001");

        assertThrows(AccessDeniedException.class, () ->
                locationService.createLocation(association.getId(), createDto, "other@test.fr")
        );
    }

    @Test
    public void update_WhenSuccess() {
        Long id = location.getId();
        LocationUpdateDto dtoToUpdate = new LocationUpdateDto("Updated Name", "Updated Address", "Manchester", "M1 1AA", association.getId());

        // Signature : locationId, associationId, dto, userEmail
        LocationReadDto updatedLoc = locationService.updateLocation(id, association.getId(), dtoToUpdate, "user@test.fr");

        assertEquals("Manchester", updatedLoc.city());
        assertEquals("Updated Name", updatedLoc.name());
    }

    @Test
    public void update_WhenAccessDenied() {
        Long id = location.getId();
        LocationUpdateDto dtoToUpdate = new LocationUpdateDto("Hacked", "Hacked", "Lyon", "69001", association.getId());

        assertThrows(AccessDeniedException.class, () ->
                locationService.updateLocation(id, association.getId(), dtoToUpdate, "other@test.fr")
        );
    }
}