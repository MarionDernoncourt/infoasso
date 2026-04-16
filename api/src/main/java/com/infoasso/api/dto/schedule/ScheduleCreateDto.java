package com.infoasso.api.dto.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.DayOfWeek;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleCreateDto {


    @NotNull
    private Long associationId;

    @NotBlank(message = "Le nom de l'activité est obligatoire.")
    private String activityName;

    @NotNull(message = "Le jour de la semaine doit être complété.")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Sélectionner une heure de début.")
    private LocalTime startTime;

    @NotNull(message = "Sélectionner une heure de fin.")
    private LocalTime endTime;

    @Min(value = 0, message = "L'âge minimum ne peut pas être négatif.")
    private Integer ageMin;

    private Integer ageMax;

    // --- Validations de cohérence ---

    @JsonIgnore
    @AssertTrue(message = "L'âge maximum doit être supérieur ou égal à l'âge minimum.")
    public boolean isAgeRangeValid() {
        if (ageMin == null || ageMax == null) return true;
        return ageMax >= ageMin;
    }

    @JsonIgnore
    @AssertTrue(message = "L'heure de fin doit être après l'heure de début.")
    public boolean isTimeRangeValid() {
        if (startTime == null || endTime == null) return true;
        return endTime.isAfter(startTime);
    }
}
