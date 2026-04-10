package com.infoasso.api.service;

import com.infoasso.api.dto.association.AssociationCreateDto;
import com.infoasso.api.dto.association.AssociationReadDto;
import com.infoasso.api.dto.association.AssociationUpdateDto;
import com.infoasso.api.exceptions.BadRequestException;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.Category;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.AssociationRepository;
import com.infoasso.api.repository.CategoryRepository;
import com.infoasso.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AssociationServiceImpl implements IAssociationService {

    private final static Logger logger = LoggerFactory.getLogger(AssociationServiceImpl.class);

    private final AssociationRepository associationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public AssociationServiceImpl(AssociationRepository associationRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.associationRepository = associationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public AssociationReadDto findById(Long id) {
        logger.info("Trying to find association with id {}", id);
        Association association = associationRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Association", id));
        logger.info("Association found : {}", association.getName());
        return mapToReadDto(association);
    }

    @Override
    public List<AssociationReadDto> findAll(String name, String category) {
        logger.info("Trying to find all associations");

        List<Association> associations;

        if (name == null && category == null) {
            associations = associationRepository.findAll();
        }
        else if (category == null) {
            associations = associationRepository.findByNameContainingIgnoreCase(name);
        }
        else if (name == null) {
            associations = associationRepository.findByCategoryLabelIgnoreCase(category);

        } else {
            associations = associationRepository.findByNameContainingIgnoreCaseAndCategoryLabelIgnoreCase(name, category);
        }

        logger.info("Association found : {}", associations.size());
        return associations.stream()
                .map(this::mapToReadDto)
                .toList();
    }


    @Override
    public AssociationReadDto createAssociation(AssociationCreateDto associationToCreate) {
        logger.info("Trying to create association with association {}", associationToCreate.getName());
        // 1. Validation métier
        checkNameUniqueness(associationToCreate.getName());
        Category category = getValidatedCategory(associationToCreate.getCategoryId());
        // 2. Mapping to Association
        Association asso = mapToEntity(associationToCreate);
        asso.setCategory(category);
        // 3. TODO: Récupérer le User connecté ici plus tard
        User owner = getValidatedOwner(associationToCreate.getUserId());
        asso.setOwner(owner);

        // 4. Persistance
        Association savedAssociation = associationRepository.save(asso);
        logger.info("Association saved with id : {}", savedAssociation.getId());
        return mapToReadDto(savedAssociation);
    }

    @Override
    public AssociationReadDto updateAssociation(Long id, AssociationUpdateDto associationToUpdate) {
        logger.info("Trying to update association {}", id);

        Association association = associationRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Association", id));

        updateEntityFromDto(associationToUpdate, association);

        Association associationUpdated = associationRepository.save(association);

        logger.info("Association updated with id : {}", association.getId());
        return mapToReadDto(associationUpdated);
    }

    @Override
    public void deleteAssociation(Long id) {
        logger.info("Trying to delete association {}", id);

        Association association = associationRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Association", id));

        associationRepository.delete(association);
        logger.info("Association {} deleted.", id);
    }

    // METHODES UTILITAIRE POUR MAPPING DTO
    // 1. Pour transformer l'entité en DTO
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
        if (association.getOwner() != null) { // ou association.getOwner() selon ton nommage
            dto.setOwnerEmail(association.getOwner().getEmail());
        }

        return dto;
    }

    // 2. Pour transformer ce qui arrive du formulaire en entité pour la DB
    private Association mapToEntity(AssociationCreateDto dto) {
        Association association = new Association();
        association.setName(dto.getName());
        association.setDescription(dto.getDescription());
        association.setEmail(dto.getEmail());
        association.setPhoneNumber(dto.getPhoneNumber());
        association.setWebsite(dto.getWebsite());

        return association;
    }

    // 3. Pour appliquer les modifs du DTO sur une entité DEJA EXISTANTE
    private void updateEntityFromDto(AssociationUpdateDto dto, Association association) {
        if (dto.getName() != null && !dto.getName().equals(association.getName())) {
            checkNameUniqueness(dto.getName());
            association.setName(dto.getName());
        }

        if (dto.getDescription() != null) association.setDescription(dto.getDescription());
        if (dto.getEmail() != null) association.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) association.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getWebsite() != null) association.setWebsite(dto.getWebsite());

        if (dto.getCategoryId() != null) {
            association.setCategory(getValidatedCategory(dto.getCategoryId()));
        }

        if (dto.getOwnerId() != null) {
            association.setOwner(getValidatedOwner(dto.getOwnerId()));
        }
    }

    // METHODES UTILITAIRES POUR VERIFICATION
    // 1. Unicité du nom de l'association
    private void checkNameUniqueness(String name) {
        if (associationRepository.existsByName(name)) {
            throw new BadRequestException("Une association avec le nom '" + name + "' existe déjà.");
        }
    }

    // 2. Existence de la catégorie
    private Category getValidatedCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RessourceNotFoundException("Category", categoryId));
    }

    // 3. Propriétaire existant
    private User getValidatedOwner(Long ownerId) {
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new RessourceNotFoundException("User", ownerId));
    }
}
