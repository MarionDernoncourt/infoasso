package com.infoasso.api.service.impl;

import com.infoasso.api.dto.association.AssociationSummaryDto;
import com.infoasso.api.dto.location.LocationReadDto;
import com.infoasso.api.dto.schedule.ScheduleCreateDto;
import com.infoasso.api.dto.schedule.ScheduleReadDto;
import com.infoasso.api.dto.schedule.ScheduleUpdateDto;
import com.infoasso.api.exceptions.ResourceAlreadyExistsException;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.Association;
import com.infoasso.api.model.DayOfWeek;
import com.infoasso.api.model.Location;
import com.infoasso.api.model.Schedule;
import com.infoasso.api.repository.AssociationRepository;
import com.infoasso.api.repository.LocationRepository;
import com.infoasso.api.repository.ScheduleRepository;
import com.infoasso.api.service.ILocationService;
import com.infoasso.api.service.IScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleServiceImpl implements IScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    private final ScheduleRepository scheduleRepository;
    private final AssociationRepository associationRepository;
    private final ILocationService  locationService;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, AssociationRepository associationRepository, ILocationService  locationService) {
        this.scheduleRepository = scheduleRepository;
        this.associationRepository = associationRepository;
        this.locationService = locationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleReadDto> findAll(Long associationId, Integer age, DayOfWeek dayOfWeek, String city) {
        logger.info("Searching for schedules for association id {}, age: {}, day: {}, city: {}", associationId, age, dayOfWeek, city);

        // Verification si association existe
        if (!getAssociationValidated(associationId)) {
            throw new ResourceNotFoundException("Association", associationId);
        }

        // Récupération des schedule
        List<Schedule> allSchedules = scheduleRepository.findByAssociationId(associationId);
        allSchedules.forEach(s -> System.out.println("DEBUG: " + s.getActivityName() + " | Desc: " + s.getDescription() + " | Loc: " + s.getLocation()));
        // Filtres
        return allSchedules.stream()
                .filter(schedule -> isMatch(schedule, age, dayOfWeek, city))
                .map(this::mapToReadDto)
                .toList();
    }

    @Override
    public ScheduleReadDto findById(Long associationId, Long scheduleId) {
        logger.info("Searching for schedule with id {} for association with id {}", scheduleId, associationId);

        // Vérification si association existe
        if (!getAssociationValidated(associationId)) {
            throw new ResourceNotFoundException("Association", associationId);
        }
        // Récupération du schedule
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Association", scheduleId));

        return mapToReadDto(schedule);
    }

    @Override
    @Transactional
    public ScheduleReadDto createSchedule(Long associationId, ScheduleCreateDto scheduleCreateDto) {
        logger.info("Creating schedule for association id {}", associationId);

        // 1. Verification existence association
        Association association = associationRepository.findById(associationId)
                .orElseThrow(() -> new ResourceNotFoundException("Association", associationId));

        // 2. Verification si Schedule existe dejà
        if (scheduleRepository.existsByAssociationIdAndActivityNameAndDayOfWeekAndStartTime(associationId, scheduleCreateDto.getActivityName(), scheduleCreateDto.getDayOfWeek(), scheduleCreateDto.getStartTime())) {
            throw new ResourceAlreadyExistsException("Un schedule avec ces informations existe déjà :" + scheduleCreateDto.getActivityName());
        }
        // 3. Creation du schedule
        Schedule schedule = mapToEntity(association, scheduleCreateDto);
        scheduleRepository.save(schedule);

        return mapToReadDto(schedule);
    }

    @Override
    public ScheduleReadDto updateSchedule(Long scheduleId, ScheduleUpdateDto scheduleUpdateDto) {
        logger.info("Trying to update schedule with id {}", scheduleId);

        // 1. Vérification si schedule existe
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", scheduleId));
        // 2. Vérification owner est bien celui connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        Association association = schedule.getAssociation();
        if (!association.getOwner().getEmail().equals(currentEmail)) {

            throw new AccessDeniedException("Tu n'es pas autorisé à modifier cette association.");
        }
        // 3. Mise à jour de l'entité
        Schedule updatedSchedule = scheduleRepository.save(updateEntityFromDto(schedule, scheduleUpdateDto));

        return mapToReadDto(updatedSchedule);
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        logger.info("Trying to delete schedule with id {}", scheduleId);

        // 1. Vérification si schedule existe
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Association", scheduleId));

        // 2. Suppression du schedule
        scheduleRepository.delete(schedule);

    }


    private boolean isMatch(Schedule s, Integer age, DayOfWeek day, String city) {
        return (age == null || (age >= s.getAgeMin() && age <= s.getAgeMax()))
                && (day == null || s.getDayOfWeek() == day)
        && (city == null || s.getLocation().getCity().equalsIgnoreCase(city));
    }

    private boolean getAssociationValidated(Long id) {
        logger.info("Get association validated for association with id {}", id);
        return associationRepository.existsById(id);
    }


    private ScheduleReadDto mapToReadDto(Schedule schedule) {
        ScheduleReadDto readDto = new ScheduleReadDto();
        readDto.setId(schedule.getId());
        readDto.setActivityName(schedule.getActivityName());
        readDto.setStartTime(schedule.getStartTime());
        readDto.setEndTime(schedule.getEndTime());
        readDto.setDayOfWeek(schedule.getDayOfWeek());
        readDto.setAgeMin(schedule.getAgeMin());
        readDto.setAgeMax(schedule.getAgeMax());
        readDto.setDescription(schedule.getDescription());
        readDto.setLocation(schedule.getLocation());
        if (schedule.getAssociation() != null) {
            AssociationSummaryDto assoDto = new AssociationSummaryDto();
            assoDto.setId(schedule.getAssociation().getId());
            assoDto.setDisplayName(schedule.getAssociation().getDisplayName());
            assoDto.setOwnerEmail(schedule.getAssociation().getOwner().getEmail());
            readDto.setAssociation(assoDto);
        }

        return readDto;
    }

    private Schedule mapToEntity(Association association, ScheduleCreateDto createDto) {
        Schedule schedule = new Schedule();
        schedule.setActivityName(createDto.getActivityName());
        schedule.setDayOfWeek(createDto.getDayOfWeek());
        schedule.setStartTime(createDto.getStartTime());
        schedule.setEndTime(createDto.getEndTime());
        schedule.setAgeMin(createDto.getAgeMin());
        schedule.setAgeMax(createDto.getAgeMax());
        schedule.setDescription(createDto.getDescription());

        // Vérification Location existe ou création
        Location locationEntity = locationService.findOrCreateEntity(createDto.getLocation());
        schedule.setLocation(locationEntity);

        schedule.setAssociation(association);

        return schedule;

    }

    private Schedule updateEntityFromDto(Schedule schedule, ScheduleUpdateDto dto) {
        if (dto.getActivityName() != null) {
            schedule.setActivityName(dto.getActivityName());
        }
        if (dto.getDayOfWeek() != null) {
            schedule.setDayOfWeek(dto.getDayOfWeek());
        }
        if (dto.getStartTime() != null) {
            schedule.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            schedule.setEndTime(dto.getEndTime());
        }
        if (dto.getAgeMin() != null) {
            schedule.setAgeMin(dto.getAgeMin());
        }
        if (dto.getAgeMax() != null) {
            schedule.setAgeMax(dto.getAgeMax());
        }
        if (dto.getDescription() != null) {
            schedule.setDescription(dto.getDescription());
        }

        if(dto.getLocation() != null) {
            // Vérification Location existe ou création
            Location locationEntity = locationService.findOrCreateEntity(dto.getLocation());
            schedule.setLocation(locationEntity);
        }

        return schedule;
    }
}
