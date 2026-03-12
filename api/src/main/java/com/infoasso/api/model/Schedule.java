package com.infoasso.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "association_id", nullable = false)
    private Association association;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le jour de la semaine doit être complété.")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Sélectionner une heure de début.")
    private LocalTime startTime;

    @NotNull(message = "Sélectionner une heure de fin.")
    private LocalTime endTime;

    @Min(value = 0 , message = "L'âge minimum ne peut pas être négatif.")
    private int ageMin;

    private int ageMax;

    @NotBlank(message = "Le nom de l'activité est obligatoire.")
    private String activityName;


}
