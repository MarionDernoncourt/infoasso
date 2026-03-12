package com.infoasso.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Le format de l'email est invalide.")
    @NotBlank(message = "L'email est obligatoire.")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Column(nullable = false)
    private String password;

    @Past(message = "La date de connexion ne peut pas être dans le futur.")
    private LocalDateTime lastLogin;

}
