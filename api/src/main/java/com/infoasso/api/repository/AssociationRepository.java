package com.infoasso.api.repository;

import com.infoasso.api.model.Association;
import com.infoasso.api.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssociationRepository extends JpaRepository<Association,Long> {
    boolean existsByName(String name);

    List<Association> findByNameContainingIgnoreCase(String name);

    List<Association> findByCategoryType(CategoryType type);

    List<Association> findByCategoryLabelIgnoreCase(String label);

    List<Association> findByNameContainingIgnoreCaseAndCategoryLabelIgnoreCase(String name, String category);

    // Pour le grand public
    List<Association> findByIsPublishedTrue();

    // Pour le back-office Mairie
    List<Association> findByIsPublishedFalse();
}
