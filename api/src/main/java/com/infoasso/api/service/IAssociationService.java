package com.infoasso.api.service;

import com.infoasso.api.dto.association.AssociationCreateDto;
import com.infoasso.api.dto.association.AssociationReadDto;
import com.infoasso.api.dto.association.AssociationUpdateDto;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.Schedule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IAssociationService {
    AssociationReadDto findById(Long id);

    List<AssociationReadDto> findAll(String name, String category);

    AssociationReadDto createAssociation(AssociationCreateDto association);

    AssociationReadDto updateAssociation(Long id, AssociationUpdateDto association);

    void deleteAssociation(Long id);
}
