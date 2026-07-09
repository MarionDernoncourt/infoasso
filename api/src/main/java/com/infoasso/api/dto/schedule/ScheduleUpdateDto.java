package com.infoasso.api.dto.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infoasso.api.model.DayOfWeek;
import com.infoasso.api.model.Location;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleUpdateDto {

    private String activityName;

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    @Min(value = 0 , message = "L'âge minimum ne peut pas être négatif.")
    private Integer ageMin;

    private Integer ageMax;

    private String description;

    private Location location;


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
