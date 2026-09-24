package com.infoasso.api.repository;

import com.infoasso.api.model.DayOfWeek;
import com.infoasso.api.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s LEFT JOIN FETCH s.location WHERE s.association.id = :id")
    List<Schedule> findByAssociationId(@Param("id") Long id);
    boolean existsByAssociationIdAndActivityNameAndDayOfWeekAndStartTime(
            Long associationId, String activityName, DayOfWeek day, LocalTime start
    );

    Optional<Schedule> findByIdAndAssociationId(Long scheduleId, Long associationId);
}
