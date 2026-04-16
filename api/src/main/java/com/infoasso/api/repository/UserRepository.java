package com.infoasso.api.repository;

import com.infoasso.api.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(@Email(message = "Le format de l'email est invalide.") @NotBlank(message = "L'email est obligatoire.") String email);

    Optional<User> findByEmail(String email);
}
