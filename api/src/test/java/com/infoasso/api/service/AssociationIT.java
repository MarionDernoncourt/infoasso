package com.infoasso.api.service;

import com.infoasso.api.dto.association.AssociationCreateDto;
import com.infoasso.api.dto.association.AssociationReadDto;
import com.infoasso.api.dto.association.AssociationUpdateDto;
import com.infoasso.api.exceptions.BadRequestException;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.*;
import com.infoasso.api.repository.AssociationRepository;
import com.infoasso.api.repository.CategoryRepository;
import com.infoasso.api.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
public class AssociationIT {

    @Autowired
    private AssociationServiceImpl associationService;
    @Autowired
    private AssociationRepository associationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    private Association association;
    private Category sportCategory;
    private User user;

    @BeforeEach
    void setup() {
        sportCategory = new Category();
        sportCategory.setLabel("SPORT");
        sportCategory.setType(CategoryType.SPORT);
        categoryRepository.save(sportCategory);


        user = new User();
        user.setEmail("user@mail.com");
        user.setPassword("Password123");
        userRepository.save(user);

        association = new Association();
        association.setName("test");
        association.setDescription("description association");
        association.setEmail("email@asso.com");
        association.setCategory(sportCategory);
        association.setOwner(user);

        associationRepository.save(association);
    }

    @Test
    public void findAll_withNoParams_whenSuccess() {
        List<AssociationReadDto> associationList = associationService.findAll(null, null);
        assertEquals(1, associationList.size());
        assertEquals("SPORT", associationList.get(0).getCategoryLabel());
    }

    @Test
    @WithMockUser
    public void findAll_withParams_whenSuccess() {
        List<AssociationReadDto> associationList = associationService.findAll("test", "SPORT");
        assertEquals(1, associationList.size());
        assertEquals("SPORT", associationList.get(0).getCategoryLabel());
        assertEquals("test", associationList.get(0).getName());
    }

    @Test
    @WithMockUser
    public void findById_whenSuccess() {
        Long id = association.getId();
        AssociationReadDto associationReadDto = associationService.findById(id);

        assertEquals(association.getCategory().getLabel(), associationReadDto.getCategoryLabel());
        assertEquals(association.getName(), associationReadDto.getName());
    }
    @Test
    @WithMockUser
    public void findById_whenNotFound() {

       assertThrows(RessourceNotFoundException.class, () -> {
           associationService.findById(999L);
       });
    }

    @Test
    @WithMockUser
    public void createAssociation_whenSuccess() {
        entityManager.flush();
        entityManager.clear();

        AssociationCreateDto associationToCreate = new AssociationCreateDto();
        associationToCreate.setName("assoToCreate");
        associationToCreate.setDescription("test creating assocation");
        associationToCreate.setEmail("created@asso.com");
        associationToCreate.setUserId(user.getId());
        associationToCreate.setCategoryId(sportCategory.getId());

        AssociationReadDto newAsso = associationService.createAssociation(associationToCreate);

        assertEquals(associationToCreate.getName(), newAsso.getName());
        assertEquals(associationToCreate.getDescription(), newAsso.getDescription());
    }

    @Test
    @WithMockUser
    public void createAssociation_whenNameAlreadyExists_shouldThrowBadRequest() {
        AssociationCreateDto dto = new AssociationCreateDto();
        dto.setName("test"); // Nom déjà utilisé dans le setup()
        dto.setCategoryId(sportCategory.getId());
        dto.setUserId(user.getId());

        assertThrows(BadRequestException.class, () -> {
            associationService.createAssociation(dto);
        });
    }

    @Test
    @WithMockUser
    public void createAssociation_whenCategoryNotFound_shouldThrow404() {
        AssociationCreateDto dto = new AssociationCreateDto();
        dto.setName("Nouvelle Asso");
        dto.setCategoryId(999L); // ID qui n'existe pas
        dto.setUserId(user.getId());

        assertThrows(RessourceNotFoundException.class, () -> {
            associationService.createAssociation(dto);
        });
    }

    @Test
    @WithMockUser
    public void createAssociation_whenUserNotFound_shouldThrow404() {
        AssociationCreateDto dto = new AssociationCreateDto();
        dto.setName("Nouvelle Asso");
        dto.setCategoryId(sportCategory.getId());
        dto.setUserId(999L); // User inconnu

        assertThrows(RessourceNotFoundException.class, () -> {
            associationService.createAssociation(dto);
        });
    }

    @Test
    @WithMockUser
    public void updateAssociation_whenSuccess() {
        Category musicCategory = new Category();
        musicCategory.setLabel("MUSIQUE");
        musicCategory.setType(CategoryType.CULTURE);
        categoryRepository.save(musicCategory);

        AssociationUpdateDto updateDto = new AssociationUpdateDto();
        updateDto.setName("test");
        updateDto.setDescription("Nouvelle description");
        updateDto.setCategoryId(musicCategory.getId());

        AssociationReadDto result = associationService.updateAssociation(association.getId(), updateDto);

        assertEquals("Nouvelle description", result.getDescription());
        assertEquals("MUSIQUE", result.getCategoryLabel());
        assertEquals("test", result.getName());
    }
    @Test
    @WithMockUser
    public void updateAssociation_whenNameTakenByOther_shouldThrowBadRequest() {
        Association otherAsso = new Association();
        otherAsso.setName("AUTRE_ASSO");
        otherAsso.setDescription("Nouvelle description");
        otherAsso.setEmail("other@asso.com");
        otherAsso.setCategory(sportCategory);
        otherAsso.setOwner(user);
        associationRepository.save(otherAsso);

        AssociationUpdateDto updateDto = new AssociationUpdateDto();
        updateDto.setName("AUTRE_ASSO");

        assertThrows(BadRequestException.class, () -> {
            associationService.updateAssociation(association.getId(), updateDto);
        });
    }

    @Test
    @WithMockUser
    public void updateAssociation_whenNotFound_shouldThrow404() {
        AssociationUpdateDto updateDto = new AssociationUpdateDto();
        updateDto.setName("Inutile");

        assertThrows(RessourceNotFoundException.class, () -> {
            associationService.updateAssociation(999L, updateDto);
        });
    }

    @Test
    @WithMockUser
    public void deleteAssociation_whenSuccess() {
        Long id = association.getId();

        associationService.deleteAssociation(id);

             boolean exists = associationRepository.existsById(id);
        assertEquals(false, exists);
    }

    @Test
    @WithMockUser
    public void deleteAssociation_whenNotFound_shouldThrow404() {
        assertThrows(RessourceNotFoundException.class, () -> {
            associationService.deleteAssociation(999L);
        });
    }
}
