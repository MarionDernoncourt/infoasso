package com.infoasso.api.service;


import com.infoasso.api.dto.association.AssociationCreateDto;
import com.infoasso.api.dto.association.AssociationReadDto;
import com.infoasso.api.dto.association.AssociationUpdateDto;
import com.infoasso.api.exceptions.BadRequestException;
import com.infoasso.api.exceptions.ResourceAlreadyExistsException;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.Category;
import com.infoasso.api.model.CategoryType;
import com.infoasso.api.model.User;
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
        sportCategory.setLabel("rugby");
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

    // --- READ TESTS ---

    @Test
    public void findAll_withNoParams_whenSuccess() {
        List<AssociationReadDto> associationList = associationService.findAll(null, null);
        assertEquals(1, associationList.size());
        assertEquals("rugby", associationList.get(0).getCategoryLabel());
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
        assertThrows(RessourceNotFoundException.class, () -> associationService.findById(999L));
    }


    @Test
    @WithMockUser
    public void createAssociation_whenSuccess() {
        AssociationCreateDto dto = new AssociationCreateDto();
        dto.setName("assoToCreate");
        dto.setDescription("test creating association");
        dto.setEmail("created@asso.com");
        dto.setUserId(user.getId());
        dto.setCategoryType(CategoryType.SPORT);
        dto.setCategoryLabel("football"); // Nouveau label

        AssociationReadDto result = associationService.createAssociation(dto);

        assertEquals("assoToCreate", result.getName());
        assertEquals("football", result.getCategoryLabel());
        // On vérifie que la catégorie a bien été créée en base
        assertEquals(true, categoryRepository.findByLabelIgnoreCaseAndType("football", CategoryType.SPORT).isPresent());
    }

    @Test
    @WithMockUser
    public void createAssociation_withExistingCategory_shouldNotCreateDuplicate() {
        long categoryCountBefore = categoryRepository.count();

        AssociationCreateDto dto = new AssociationCreateDto();
        dto.setName("Une Autre Asso");
        dto.setDescription("Description");
        dto.setEmail("another@mail.com");
        dto.setUserId(user.getId());
        dto.setCategoryType(CategoryType.SPORT);
        dto.setCategoryLabel("rugby");

        associationService.createAssociation(dto);

        assertEquals(categoryCountBefore, categoryRepository.count());
    }

    @Test
    @WithMockUser
    public void createAssociation_whenNameAlreadyExists_shouldThrownResourceAlreadyExistsException() {
        AssociationCreateDto dto = new AssociationCreateDto();
        dto.setName("test");
        dto.setCategoryLabel("rugby");
        dto.setCategoryType(CategoryType.SPORT);
        dto.setUserId(user.getId());

        assertThrows(ResourceAlreadyExistsException.class, () -> associationService.createAssociation(dto));
    }

    // --- UPDATE TESTS ---

    @Test
    @WithMockUser
    public void updateAssociation_whenSuccess() {
        AssociationUpdateDto updateDto = new AssociationUpdateDto();
        updateDto.setName("test");
        updateDto.setDescription("Nouvelle description");
        updateDto.setCategoryLabel("MUSIQUE"); // Nouveau label
        updateDto.setCategoryType(CategoryType.CULTURE);

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
        otherAsso.setCategory(sportCategory);
        otherAsso.setOwner(user);
        otherAsso.setDescription("autre asso");
        otherAsso.setEmail("other@asso.com");
        associationRepository.save(otherAsso);

        AssociationUpdateDto updateDto = new AssociationUpdateDto();
        updateDto.setName("AUTRE_ASSO");

        assertThrows(BadRequestException.class, () -> associationService.updateAssociation(association.getId(), updateDto));
    }

    @Test
    @WithMockUser
    public void deleteAssociation_whenSuccess() {
        Long id = association.getId();
        associationService.deleteAssociation(id);
        assertEquals(false, associationRepository.existsById(id));
    }
}