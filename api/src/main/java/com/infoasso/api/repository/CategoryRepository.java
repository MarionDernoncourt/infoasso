package com.infoasso.api.repository;

import com.infoasso.api.model.Category;
import com.infoasso.api.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Optional<Category> findByLabelIgnoreCaseAndType(String label, CategoryType categoryType);
}
