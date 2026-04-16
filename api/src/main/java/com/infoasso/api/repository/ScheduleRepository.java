package com.infoasso.api.repository;

import com.infoasso.api.model.DayOfWeek;
import com.infoasso.api.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByAssociationId(Long id);

    boolean existsByAssociationIdAndActivityNameAndDayOfWeekAndStartTime(
            Long associationId, String activityName, DayOfWeek day, LocalTime start
    );
}
