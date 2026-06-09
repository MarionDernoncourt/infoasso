package com.infoasso.api.service.impl;

import com.infoasso.api.dto.association.AssociationCreateDto;
import com.infoasso.api.dto.association.AssociationReadDto;
import com.infoasso.api.dto.association.AssociationUpdateDto;
import com.infoasso.api.exceptions.ResourceAlreadyExistsException;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.Category;
import com.infoasso.api.model.CategoryType;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.AssociationRepository;
import com.infoasso.api.repository.CategoryRepository;
import com.infoasso.api.repository.UserRepository;
import com.infoasso.api.service.IAssociationService;
import com.infoasso.api.service.IRnaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssociationServiceImpl implements IAssociationService {

    private final static Logger logger = LoggerFactory.getLogger(AssociationServiceImpl.class);

    private final AssociationRepository associationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final IRnaService rnaService;

    @Override
    public AssociationReadDto findById(Long id) {
        logger.info("Recherche de l'association avec l'ID : {}", id);
        Association association = associationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Association", id));
        return mapToReadDto(association);
    }

    @Override
    public List<AssociationReadDto> findAll(String name, String category) {
        logger.info("Recherche multi-critères - Nom: {}, Catégorie: {}", name, category);

        List<Association> associations;

        if (name == null && category == null) {
            associations = associationRepository.findAll();
        } else if (category == null) {
            associations = associationRepository.findByDisplayNameContainingIgnoreCase(name);
        } else if (name == null) {
            associations = associationRepository.findByCategoryLabelIgnoreCase(category);
        } else {
            associations = associationRepository.findByDisplayNameContainingIgnoreCaseAndCategoryLabelIgnoreCase(name, category);
        }

        return associations.stream()
                .map(this::mapToReadDto)
                .toList();
    }

    @Override
    @Transactional
    public AssociationReadDto createAssociation(AssociationCreateDto dto) {
        logger.info("Début création association pour le RNA : {}", dto.getRnaNumber());
        logger.info("DTO reçu du Front : streetAddress={}, zipCode={}, city={}",
                dto.getStreetAddress(), dto.getZipCode(), dto.getCity());
        // 1. Vérifier si l'association n'existe pas déjà (par numéro RNA)
        if (associationRepository.existsByRnaNumber(dto.getRnaNumber())) {
            throw new ResourceAlreadyExistsException("L'association avec le RNA " + dto.getRnaNumber() + " existe déjà en base.");
        }

        // 2. Récupérer l'utilisateur connecté via le token JWT (le futur Owner)
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur connecté non trouvé en base."));

              // 4. Préparer l'entité
        Association asso = new Association();
        asso.setRnaNumber(dto.getRnaNumber());
        asso.setOfficialName(dto.getOfficialName());
        asso.setDisplayName(dto.getDisplayName()); // Nom personnalisé (ex: "Club Hockey Loos")
        asso.setDescription(dto.getDescription());
        asso.setEmail(dto.getEmail());
        asso.setOwner(owner);
        asso.setStreetAddress(dto.getStreetAddress());
        asso.setZipCode(dto.getZipCode());
        asso.setCity(dto.getCity());
        asso.setPhoneNumber(dto.getPhoneNumber());

        // 5. Gérer la catégorie
        Category category = getOrCreateCategoryEntity(dto.getCategoryLabel(), dto.getCategoryType());
        asso.setCategory(category);

        Association savedAsso = associationRepository.save(asso);
        logger.info("Association créée avec succès, ID : {}", savedAsso.getId());

        return mapToReadDto(savedAsso);
    }

    @Override
    @Transactional
    public AssociationReadDto updateAssociation(Long id, AssociationUpdateDto dto) {
        logger.info("Mise à jour de l'association ID : {}", id);

        Association association = associationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Association", id));
// Vérification du nom officiel
        if (dto.getOfficialName() != null && !dto.getOfficialName().equals(association.getOfficialName())) {
            boolean exists = associationRepository.existsByOfficialNameAndIdNot(dto.getOfficialName(), id);
            if (exists) {
                throw new ResourceAlreadyExistsException("Le nom officiel est déjà utilisé par une autre association");
            }
            association.setOfficialName(dto.getOfficialName());
        }

        // Mise à jour sélective
        if (dto.getDisplayName() != null) association.setDisplayName(dto.getDisplayName());
        if (dto.getDescription() != null) association.setDescription(dto.getDescription());
        if (dto.getEmail() != null) association.setEmail(dto.getEmail());

        if (dto.getCategoryLabel() != null && dto.getCategoryType() != null) {
            Category category = getOrCreateCategoryEntity(dto.getCategoryLabel(), dto.getCategoryType());
            association.setCategory(category);
        }

        return mapToReadDto(associationRepository.save(association));
    }

    @Override
    @Transactional
    public void deleteAssociation(Long id) {
        logger.info("Suppression de l'association ID : {}", id);
        Association association = associationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Association", id));
        associationRepository.delete(association);
    }

    @Override
    public List<AssociationReadDto> findAllAssociationsByUserEmail(String email) {
        logger.info("Récupération de la liste des associations du user {}", email);

        // 1. Récupération de TOUTES les assos de l'utilisateur
        List<Association> associations = associationRepository.findByOwnerEmail(email);

        // 2. Transformation en DTOs
        return associations.stream()
                .map(this::mapToReadDto)
                .toList();
    }

    // --- HELPER METHODS ---

    private AssociationReadDto mapToReadDto(Association association) {
        AssociationReadDto dto = new AssociationReadDto();
        dto.setId(association.getId());
        dto.setRnaNumber(association.getRnaNumber());
        dto.setOfficialName(association.getOfficialName());
        dto.setDisplayName(association.getDisplayName());
        dto.setDescription(association.getDescription());
        dto.setEmail(association.getEmail());
        dto.setStreetAddress(association.getStreetAddress());
        dto.setZipCode(association.getZipCode());
        dto.setCity(association.getCity());
        dto.setPhoneNumber(association.getPhoneNumber());
    dto.setWebsite(association.getWebsite());

        if (association.getCategory() != null) {
            dto.setCategoryType(association.getCategory().getType());
            dto.setCategoryLabel(association.getCategory().getLabel());
        }

        if (association.getOwner() != null) {
            dto.setOwnerEmail(association.getOwner().getEmail());
        }
        return dto;
    }

    private Category getOrCreateCategoryEntity(String label, CategoryType type) {
        return categoryRepository.findByLabelIgnoreCaseAndType(label, type)
                .orElseGet(() -> {
                    Category newCat = new Category();
                    newCat.setLabel(label);
                    newCat.setType(type);
                    return categoryRepository.save(newCat);
                });
    }
}