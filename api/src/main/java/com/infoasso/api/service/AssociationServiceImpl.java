package com.infoasso.api.service;

import com.infoasso.api.dto.association.AssociationCreateDto;
import com.infoasso.api.dto.association.AssociationReadDto;
import com.infoasso.api.dto.association.AssociationUpdateDto;
import com.infoasso.api.exceptions.ResourceAlreadyExistsException;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.Category;
import com.infoasso.api.model.CategoryType;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.AssociationRepository;
import com.infoasso.api.repository.CategoryRepository;
import com.infoasso.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssociationServiceImpl implements IAssociationService {

    private final static Logger logger = LoggerFactory.getLogger(AssociationServiceImpl.class);

    private final AssociationRepository associationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ICategoryService categoryService;

    public AssociationServiceImpl(AssociationRepository associationRepository, UserRepository userRepository, CategoryRepository categoryRepository, ICategoryService categoryService) {
        this.associationRepository = associationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
    }

    @Override
    public AssociationReadDto findById(Long id) {
        logger.info("Trying to find association with id {}", id);
        Association association = associationRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Association", id));
        return mapToReadDto(association);
    }

    @Override
    public List<AssociationReadDto> findAll(String name, String category) {
        logger.info("Searching associations with name: {} and category: {}", name, category);

        List<Association> associations;

        if (name == null && category == null) {
            associations = associationRepository.findAll();
        } else if (category == null) {
            associations = associationRepository.findByNameContainingIgnoreCase(name);
        } else if (name == null) {
            associations = associationRepository.findByCategoryLabelIgnoreCase(category);
        } else {
            associations = associationRepository.findByNameContainingIgnoreCaseAndCategoryLabelIgnoreCase(name, category);
        }

        return associations.stream()
                .map(this::mapToReadDto)
                .toList();
    }

    @Override
    @Transactional
    public AssociationReadDto createAssociation(AssociationCreateDto dto) {
        logger.info("Creating association: {}", dto.getDisplayName());

        checkNameUniqueness(dto.getDisplayName());

        Category category = getOrCreateCategoryEntity(
                dto.getCategoryLabel(),
                dto.getCategoryType()
        );
        Association asso = mapToEntity(dto);
        asso.setCategory(category);
        asso.setOwner(getValidatedOwner(dto.getUserId()));

        return mapToReadDto(associationRepository.save(asso));
    }

    @Override
    @Transactional
    public AssociationReadDto updateAssociation(Long id, AssociationUpdateDto dto) {
        logger.info("Updating association id: {}", id);

        Association association = associationRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Association", id));

        updateEntityFromDto(dto, association);

        return mapToReadDto(associationRepository.save(association));
    }

    @Override
    @Transactional
    public void deleteAssociation(Long id) {
        logger.info("Deleting association id: {}", id);
        Association association = associationRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Association", id));
        associationRepository.delete(association);
    }

    // --- MAPPING METHODS ---

    private AssociationReadDto mapToReadDto(Association association) {
        AssociationReadDto dto = new AssociationReadDto();
        dto.setId(association.getId());
        dto.setName(association.getName());
        dto.setDescription(association.getDescription());
        dto.setEmail(association.getEmail());
        dto.setPhoneNumber(association.getPhoneNumber());
        dto.setWebsite(association.getWebsite());

        if (association.getCategory() != null) {
            dto.setCategoryLabel(association.getCategory().getLabel());
        }
        if (association.getOwner() != null) {
            dto.setOwnerEmail(association.getOwner().getEmail());
        }
        return dto;
    }

    private Association mapToEntity(AssociationCreateDto dto) {
        Association association = new Association();
        association.setName(dto.getName());
        association.setDescription(dto.getDescription());
        association.setEmail(dto.getEmail());
        association.setPhoneNumber(dto.getPhoneNumber());
        association.setWebsite(dto.getWebsite());
        return association;
    }

    private void updateEntityFromDto(AssociationUpdateDto dto, Association association) {
        if (dto.getName() != null && !dto.getName().equals(association.getName())) {
            checkNameUniqueness(dto.getName());
            association.setName(dto.getName());
        }

        if (dto.getDescription() != null) association.setDescription(dto.getDescription());
        if (dto.getEmail() != null) association.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) association.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getWebsite() != null) association.setWebsite(dto.getWebsite());

        // Mise à jour de la catégorie (Label + Type)
        if (dto.getCategoryLabel() != null && dto.getCategoryType() != null) {
            Category category = getOrCreateCategoryEntity(dto.getCategoryLabel(), dto.getCategoryType());
            association.setCategory(category);
        }

        if (dto.getOwnerId() != null) {
            association.setOwner(getValidatedOwner(dto.getOwnerId()));
        }
    }

    // --- UTILS ---

    private void checkNameUniqueness(String name) {
        if (associationRepository.existsByName(name)) {
            throw new ResourceAlreadyExistsException("Une association avec le nom '" + name + "' existe déjà.");
        }
    }

    private User getValidatedOwner(Long ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new RessourceNotFoundException("User", ownerId));
    }

    public Category getOrCreateCategoryEntity(String label, CategoryType type) {
        return categoryRepository.findByLabelIgnoreCaseAndType(label, type)
                .orElseGet(() -> {
                    Category newCat = new Category();
                    newCat.setLabel(label);
                    newCat.setType(type);
                    return categoryRepository.save(newCat);
                });
    }
}