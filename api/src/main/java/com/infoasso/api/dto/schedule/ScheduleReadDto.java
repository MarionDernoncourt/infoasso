package com.infoasso.api.dto.schedule;

import com.infoasso.api.dto.association.AssociationSummaryDto;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.DayOfWeek;
import com.infoasso.api.model.Location;
import jakarta.persistence.*;
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
public class ScheduleReadDto {

    private Long id;

    private AssociationSummaryDto association;

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer ageMin;

    private Integer ageMax;

    private String activityName;

    private String description;

    private Location location;
    }
