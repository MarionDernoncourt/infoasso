package com.infoasso.api.service;


import com.infoasso.api.dto.association.AssociationCreateDto;
import com.infoasso.api.dto.association.AssociationReadDto;
import com.infoasso.api.dto.association.AssociationUpdateDto;
import com.infoasso.api.exceptions.ResourceAlreadyExistsException;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.*;
import com.infoasso.api.repository.AssociationRepository;
import com.infoasso.api.repository.CategoryRepository;
import com.infoasso.api.repository.UserRepository;
import com.infoasso.api.service.impl.AssociationServiceImpl;
import com.infoasso.api.service.impl.RnaServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@ActiveProfiles("test")
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
    @MockBean
    private RnaServiceImpl rnaService;

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
        user.setEmail("mario@example.com");
        user.setPassword("Password123");
        user.setRole(Role.ROLE_USER);
        user.setGdprConsent(true);
        userRepository.save(user);

        association = new Association();
        association.setRnaNumber("W123456789");
        association.setOfficialName("Hockey Club Loos");
        association.setDisplayName("test");
        association.setOfficialName("CLUB DE HOCKEY LOOS ASSOCIATION");
        association.setDescription("description association");
        association.setEmail("email@asso.com");
        association.setCategory(sportCategory);
        association.setOwner(user);

        Map<String, String> mockRnaData = new HashMap<>();
        mockRnaData.put("officialName", "ASSOCIATION TEST OFFICIELLE");
        mockRnaData.put("objet", "Description générique pour les tests");
        mockRnaData.put("adrs_numvoie", "10");
        mockRnaData.put("adrs_typevoie", "RUE");
        mockRnaData.put("adrs_libvoie", "DE LA PAIX");
        mockRnaData.put("adrs_codepostal", "59120");
        mockRnaData.put("libcom", "LOOS");

        Mockito.when(rnaService.getAssociationData(anyString())).thenReturn(mockRnaData);

        associationRepository.save(association);
    }

    // --- READ TESTS ---

    @Test
    public void findAll_withNoParams_whenSuccess() {
        List<AssociationReadDto> associationList = associationService.findAll(null, null, null, null);
        assertEquals(1, associationList.size());
        assertEquals("rugby", associationList.get(0).getCategoryLabel());
    }

    @Test
    @WithMockUser
    public void findById_whenSuccess() {
        Long id = association.getId();
        AssociationReadDto associationReadDto = associationService.findById(id);

        assertEquals(association.getCategory().getLabel(), associationReadDto.getCategoryLabel());
        assertEquals(association.getDisplayName(), associationReadDto.getDisplayName());
    }

    @Test
    @WithMockUser
    public void findById_whenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> associationService.findById(999L));
    }


    @Test
    @WithMockUser(username = "mario@example.com")
    public void createAssociation_whenSuccess() {
        AssociationCreateDto dto = new AssociationCreateDto();
        dto.setRnaNumber("W000000001");
        dto.setOfficialName("test Club Loos");
        dto.setDisplayName("assoToCreate");
        dto.setDescription("test creating association");
        dto.setEmail("created@asso.com");
        dto.setCategoryType(CategoryType.SPORT);
        dto.setCategoryLabel("football"); // Nouveau label

        AssociationReadDto result = associationService.createAssociation(dto);

        assertEquals("assoToCreate", result.getDisplayName());
        assertEquals("football", result.getCategoryLabel());
        // On vérifie que la catégorie a bien été créée en base
        assertEquals(true, categoryRepository.findByLabelIgnoreCaseAndType("football", CategoryType.SPORT).isPresent());
    }

    @Test
    @WithMockUser(username = "mario@example.com")
    public void createAssociation_withExistingCategory_shouldNotCreateDuplicate() {
        long categoryCountBefore = categoryRepository.count();

        AssociationCreateDto dto = new AssociationCreateDto();
        dto.setRnaNumber("W123456790");
        dto.setOfficialName("Autre Club Loos");
        dto.setDisplayName("Une Autre Asso");
        dto.setDescription("Description");
        dto.setEmail("another@mail.com");
        dto.setCategoryType(CategoryType.SPORT);
        dto.setCategoryLabel("rugby");

        associationService.createAssociation(dto);

        assertEquals(categoryCountBefore, categoryRepository.count());
    }

    @Test
    @WithMockUser(username = "mario@example.com")
    public void createAssociation_whenNameAlreadyExists_shouldThrownResourceAlreadyExistsException() {
        AssociationCreateDto dto = new AssociationCreateDto();
        dto.setDisplayName("test");
        dto.setCategoryLabel("rugby");
        dto.setCategoryType(CategoryType.SPORT);

        assertThrows(RuntimeException.class, () -> associationService.createAssociation(dto));
    }

    // --- UPDATE TESTS ---

    @Test
    @WithMockUser(username = "mario@example.com", roles = {"USER"})
    public void updateAssociation_whenSuccess() {
        AssociationUpdateDto updateDto = new AssociationUpdateDto();
        updateDto.setDisplayName("test");
        updateDto.setDescription("Nouvelle description");
        updateDto.setCategoryLabel("MUSIQUE"); // Nouveau label
        updateDto.setCategoryType(CategoryType.CULTURE);

        AssociationReadDto result = associationService.updateAssociation(association.getId(), updateDto, "mario@example.com");

        assertEquals("Nouvelle description", result.getDescription());
        assertEquals("MUSIQUE", result.getCategoryLabel());
        assertEquals("test", result.getDisplayName());
    }

    @Test
    @WithMockUser(username = "mario@example.com")
    public void updateAssociation_whenNameTakenByOther_shouldThrowBadRequest() {
        Association other = new Association();
        other.setOfficialName("NOM_OFFICIEL_UNIQUE");
        other.setDisplayName("Asso 2");
        other.setRnaNumber("W999888777");
        other.setCategory(sportCategory);
        other.setOwner(user);
        other.setEmail("other@test.com");
        associationRepository.save(other);

        entityManager.flush();

        AssociationUpdateDto updateDto = new AssociationUpdateDto();
        updateDto.setOfficialName("NOM_OFFICIEL_UNIQUE");
        assertThrows(ResourceAlreadyExistsException.class, () -> associationService.updateAssociation(association.getId(), updateDto, "mario@example.com"));
    }

    @Test
    @WithMockUser(username = "mario@example.com")
    public void deleteAssociation_whenSuccess() {
        Long id = association.getId();
        associationService.deleteAssociation(id, "mario@example.com");
        assertEquals(false, associationRepository.existsById(id));
    }
}