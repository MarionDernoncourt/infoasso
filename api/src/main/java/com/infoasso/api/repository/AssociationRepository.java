package com.infoasso.api.repository;

import com.infoasso.api.model.Association;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface AssociationRepository extends JpaRepository<Association,Long> {
}
