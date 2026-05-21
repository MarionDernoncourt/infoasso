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
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(required = false) String category
    ) {
        logger.info("GET / : Request received for all associations");
        List<AssociationReadDto> associations = associationService.findAll(name, category);
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssociationReadDto> createAssociation(@Valid @RequestBody AssociationCreateDto association) {
        logger.info("POST / : Request received to create {}", association.getDisplayName());
        AssociationReadDto associationCreated = associationService.createAssociation(association);
        logger.info("POST / : Response 201 CREATED : The association {} has been created", associationCreated.getDisplayName());
        return ResponseEntity.status(HttpStatus.CREATED).body(associationCreated);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssociationReadDto> updateAssociation(@PathVariable Long id, @Valid @RequestBody AssociationUpdateDto association) {
        logger.info("PUT / {} : Request received to update {}", id, association.getOfficialName());
        AssociationReadDto associationUpdated = associationService.updateAssociation(id, association);
        logger.info("PUT/ {} : Response 200 OK : The association {} is updated", id, associationUpdated.getDisplayName());
        return ResponseEntity.status(HttpStatus.OK).body(associationUpdated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAssociation(@PathVariable Long id) {
        logger.info("DELETE / {} : Request received to delete {}", id, id);
        associationService.deleteAssociation(id);
        logger.info("DELETE / {} : Response 204 No Content", id);
        return ResponseEntity.noContent().build();
    }

}
