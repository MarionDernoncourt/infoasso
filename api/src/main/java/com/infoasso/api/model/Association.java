package com.infoasso.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "associations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Le nom ne peut pas être vide.")
    private String name;

    @NotBlank(message = "La description doit être complétée.")
    private String description;

    @NotBlank(message = "L'email est obligatoire.")
    private String email;

    @Pattern(regexp = "^(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}$",
            message = "Le numéro de téléphone n'est pas valide")
    private String phoneNumber;

    private String website;


}
