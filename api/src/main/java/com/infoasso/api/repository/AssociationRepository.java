package com.infoasso.api.repository;

import com.infoasso.api.model.Association;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssociationRepository extends JpaRepository<Association,Long> {
    boolean existsByName(String name);

    List<Association> findByNameContainingIgnoreCase(String name);
    List<Association> findByCategoryLabelIgnoreCase(String category);
    List<Association> findByNameContainingIgnoreCaseAndCategoryLabelIgnoreCase(String name, String category);
}
