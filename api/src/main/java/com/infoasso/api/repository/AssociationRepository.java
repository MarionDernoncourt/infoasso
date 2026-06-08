package com.infoasso.api.repository;

import com.infoasso.api.model.Association;
import com.infoasso.api.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssociationRepository extends JpaRepository<Association,Long> {


    List<Association> findByCategoryLabelIgnoreCase(String label);

    List<Association> findByDisplayNameContainingIgnoreCase(String name);

    List<Association> findByDisplayNameContainingIgnoreCaseAndCategoryLabelIgnoreCase(String name, String category);

    boolean existsByRnaNumber(@NotBlank(message = "Le numéro RNA est obligatoire.") @Pattern(regexp = "^W\\d{9}$", message = "Le format du RNA est invalide (ex: W123456789)") String rnaNumber);

    boolean existsByOfficialNameAndIdNot(String officialName, Long id);

    List<Association> findByOwnerEmail(String email);

// Pour le grand public
List<Association> findByIsPublishedTrue();

// Pour le back-office Mairie
List<Association> findByIsPublishedFalse();
}