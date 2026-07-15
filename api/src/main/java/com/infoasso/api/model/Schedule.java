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
@Table(name = "schedules", uniqueConstraints = {
        @UniqueConstraint(name = "uk_schedule_unique_session",
                columnNames = {"association_id", "activity_name", "day_of_week", "start_time"})
})
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
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
    private Integer ageMin;

    private Integer ageMax;

    @NotBlank(message = "Le nom de l'activité est obligatoire.")
    private String activityName;

    private String description;

    @ManyToOne
    @JoinColumn(name="location_id")
    @NotNull(message = "Le lieu de l'activité est obligatoire, veuillez entrer le nom de la ville au minimum.")
    private Location location;

}
