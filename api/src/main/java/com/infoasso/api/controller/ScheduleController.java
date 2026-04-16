package com.infoasso.api.controller;

import com.infoasso.api.dto.schedule.ScheduleCreateDto;
import com.infoasso.api.dto.schedule.ScheduleReadDto;
import com.infoasso.api.dto.schedule.ScheduleUpdateDto;
import com.infoasso.api.model.DayOfWeek;
import com.infoasso.api.service.IScheduleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("api/associations/{id}/schedules")
public class ScheduleController {

    private final static Logger logger =  LoggerFactory.getLogger(ScheduleController.class);

    private IScheduleService scheduleService;

    public ScheduleController(IScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("")
    public ResponseEntity<List<ScheduleReadDto>> findAll(
            @PathVariable Long id,
            @RequestParam (required = false) Integer age,
            @RequestParam (required = false) DayOfWeek dayOfWeek,
            @RequestParam (required = false )LocalTime startTime ) {
        logger.info("GET: / : Request received for association with id " + id);
        List<ScheduleReadDto> schedules = scheduleService.findAll(id, age, dayOfWeek, startTime);
        logger.info("GET / : Response 200 OK : Nombre de schedules : " + schedules.size());
        return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    @PostMapping("")
    public ResponseEntity<ScheduleReadDto> createSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleCreateDto scheduleCreateDto) {
        logger.info("POST: / : Request received for create schedule for association with id " + id);
        ScheduleReadDto createdSchedule = scheduleService.createSchedule(id, scheduleCreateDto);
        logger.info("POST : / : Response 201 CREATED : Schedule created with id " + createdSchedule.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleReadDto> updateSchedule(@PathVariable Long scheduleId, @Valid @RequestBody ScheduleUpdateDto scheduleUpdateDto) {
        logger.info("PUT: / : Request received for update schedule with id " + scheduleId);
        ScheduleReadDto updatedSchedule = scheduleService.updateSchedule(scheduleId, scheduleUpdateDto);
        logger.info("PUT : / : Response 200 OK : Schedule updated with id " + updatedSchedule.getId());
        return ResponseEntity.status(HttpStatus.OK).body(updatedSchedule);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        logger.info("DELETE: / : Request received to delete schedule with id " + scheduleId);
        scheduleService.deleteSchedule(scheduleId);
        logger.info("DELETE : / : Response 204 NO CONTENT");
        return ResponseEntity.noContent().build();
    }
}
