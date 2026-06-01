package com.infoasso.api.service;

import com.infoasso.api.dto.schedule.ScheduleCreateDto;
import com.infoasso.api.dto.schedule.ScheduleReadDto;
import com.infoasso.api.dto.schedule.ScheduleUpdateDto;
import com.infoasso.api.model.DayOfWeek;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public interface IScheduleService {
    List<ScheduleReadDto> findAll(Long id, Integer age, DayOfWeek dayOfWeek, LocalTime startTime);
    ScheduleReadDto findById(Long id, Long scheduleId);

    ScheduleReadDto createSchedule(Long id, ScheduleCreateDto scheduleCreateDto);

    ScheduleReadDto updateSchedule(Long id,ScheduleUpdateDto scheduleUpdateDto);

    void deleteSchedule(Long scheduleId);


}
