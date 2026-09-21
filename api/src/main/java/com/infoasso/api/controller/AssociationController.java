package com.infoasso.api.controller;

import com.infoasso.api.dto.association.AssociationCreateDto;
import com.infoasso.api.dto.association.AssociationReadDto;
import com.infoasso.api.dto.association.AssociationUpdateDto;
import com.infoasso.api.service.IAssociationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/associations")
public class AssociationController {

    private final static Logger logger = LoggerFactory.getLogger(AssociationController.class);

    private final IAssociationService associationService;

    public AssociationController(IAssociationService associationService) {
        this.associationService = associationService;
    }

    @GetMapping("")
    public ResponseEntity<List<AssociationReadDto>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String categoryTypes,
            @RequestParam(required = false) Integer age
    ) {
        logger.info("GET / : Request received for all associations");
        List<AssociationReadDto> associations = associationService.findAll(name, city, categoryTypes, age);
        logger.info("GET / : Response 200 OK : Number of associations : " + associations.size());
        return ResponseEntity.status(HttpStatus.OK).body(associations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociationReadDto> findById(@PathVariable Long id) {
        logger.info("GET /{} : Request received for association id {}", id, id);
        AssociationReadDto association = associationService.findById(id);
        logger.info("GET /{} : Response 200 OK : The association {} is found", id, association.getOfficialName());
        return ResponseEntity.status(HttpStatus.OK).body(association);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AssociationReadDto> createAssociation(@Valid @RequestBody AssociationCreateDto association) {
        logger.info("POST / : Request received to create {}", association.getDisplayName());
        AssociationReadDto associationCreated = associationService.createAssociation(association);
        logger.info("POST / : Response 201 CREATED : The association {} has been created", associationCreated.getDisplayName());
        return ResponseEntity.status(HttpStatus.CREATED).body(associationCreated);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AssociationReadDto> updateAssociation(@PathVariable Long id, @Valid @RequestBody AssociationUpdateDto association, Principal principal) {
        logger.info("PUT / {} : Request received to update {}", id, association.getOfficialName());
        String userEmail = principal.getName();
        AssociationReadDto associationUpdated = associationService.updateAssociation(id, association, userEmail);
        logger.info("PUT/ {} : Response 200 OK : The association {} is updated", id, associationUpdated.getDisplayName());
        return ResponseEntity.status(HttpStatus.OK).body(associationUpdated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteAssociation(@PathVariable Long id) {
        logger.info("DELETE / {} : Request received to delete {}", id, id);
        associationService.deleteAssociation(id);
        logger.info("DELETE / {} : Response 204 No Content", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-associations")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AssociationReadDto>> findAllMyAssociation(Authentication auth) {
        logger.info("GET /my-associations : Request received to get all associations for user {}",auth.getName());
        String userEmail = auth.getName();

        List<AssociationReadDto> associations = associationService.findAllAssociationsByUserEmail(userEmail);

        if(associations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        logger.info("GET /my associations : Response 200 OK — Found {} association(s)", associations.size());
        return ResponseEntity.status(HttpStatus.OK).body(associations);

    }


}
