package com.infoasso.api.service;

import com.infoasso.api.dto.schedule.ScheduleCreateDto;
import com.infoasso.api.dto.schedule.ScheduleReadDto;
import com.infoasso.api.dto.schedule.ScheduleUpdateDto;
import com.infoasso.api.exceptions.ResourceAlreadyExistsException;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.*;
import com.infoasso.api.repository.AssociationRepository;
import com.infoasso.api.repository.CategoryRepository;
import com.infoasso.api.repository.ScheduleRepository;
import com.infoasso.api.repository.UserRepository;
import com.infoasso.api.service.impl.ScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ScheduleServiceIT {

    @Autowired
    private ScheduleServiceImpl scheduleService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssociationRepository associationRepository;

    private Schedule schedule;
    private Association association;
    private Category category;
    private User user;
    private List<ScheduleReadDto> schedules = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        category = new Category();
        category.setLabel("rugby");
        category.setType(CategoryType.SPORT);
        categoryRepository.save(category);

        user = new User();
        user.setEmail("user@mail.com");
        user.setPassword("Password123");
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        association = new Association();
        association.setRnaNumber("W123456789");
        association.setOfficialName("footbal club");
        association.setDisplayName("footbal club");
        association.setDescription("description association");
        association.setEmail("email@asso.com");
        association.setCategory(category);
        association.setOwner(user);
        associationRepository.save(association);

        schedule = new Schedule();
        schedule.setActivityName("Baby football");
        schedule.setAgeMin(3);
        schedule.setAgeMax(5);
        schedule.setAssociation(this.association);
        schedule.setDayOfWeek(DayOfWeek.Lundi);
        schedule.setStartTime(LocalTime.of(15, 00));
        schedule.setEndTime(LocalTime.of(15, 30));
        scheduleRepository.save(schedule);
    }

    @Test
    public void findAll_noFilters_whenSuccess() {

        Long associationId = association.getId();
        schedules = scheduleService.findAll(associationId, null, null, null);

        assertEquals(1, schedules.size());
    }

    @Test
    public void findAll_Filters_whenSuccess() {
        Long associationId = association.getId();
        schedules = scheduleService.findAll(associationId, 3, DayOfWeek.Lundi, LocalTime.of(15, 00));

        assertEquals(1, schedules.size());
    }

    @Test
    public void findAll_associationNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> scheduleService.findAll(3L, null, null, null));
    }

    @Test
    public void findAll_whenFiltersDontMatch_shouldReturnEmptyList() {
        Long associationId = association.getId();
        List<ScheduleReadDto> schedules = scheduleService.findAll(associationId, 20, DayOfWeek.Dimanche, LocalTime.of(10, 0));

        assertEquals(0, schedules.size());
    }

    @Test
    public void findById_whenSuccess() {
        Long associationId = association.getId();
        Long scheduleId = schedule.getId();

        // Exécution du vrai service connecté à la BDD de test
        ScheduleReadDto foundSchedule = scheduleService.findById(associationId, scheduleId);

        // Assertions
        assertEquals(scheduleId, foundSchedule.getId());
        assertEquals("Baby football", foundSchedule.getActivityName());
        assertEquals(DayOfWeek.Lundi, foundSchedule.getDayOfWeek());
    }

    @Test
    public void findById_whenAssociationNotFound_shouldThrowNotFound() {
        Long invalidAssociationId = 999L; // Cet ID n'existe pas en BDD
        Long scheduleId = schedule.getId();

        // On vérifie que la validation de l'association bloque bien la requête
        assertThrows(ResourceNotFoundException.class, () -> {
            scheduleService.findById(invalidAssociationId, scheduleId);
        });
    }

    @Test
    public void findById_whenScheduleNotFound_shouldThrowNotFound() {
        Long associationId = association.getId();
        Long invalidScheduleId = 999L; // Ce schedule n'existe pas en BDD

        // On vérifie que le .orElseThrow() du Repository fait bien son job
        assertThrows(ResourceNotFoundException.class, () -> {
            scheduleService.findById(associationId, invalidScheduleId);
        });
    }

    @Test
    public void createSchedule_whenSuccess() {
        ScheduleCreateDto scheduleCreateDto = new ScheduleCreateDto();
        {
            scheduleCreateDto.setActivityName("Junior football");
            scheduleCreateDto.setAgeMin(6);
            scheduleCreateDto.setAgeMax(8);
            scheduleCreateDto.setAssociationId(association.getId());
            scheduleCreateDto.setDayOfWeek(DayOfWeek.Lundi);
            scheduleCreateDto.setStartTime(LocalTime.of(17, 00));
            scheduleCreateDto.setEndTime(LocalTime.of(18, 30));

            ScheduleReadDto scheduleReadDto = scheduleService.createSchedule(association.getId(), scheduleCreateDto);

            assertEquals(scheduleReadDto.getActivityName(), scheduleCreateDto.getActivityName());
            assertEquals(2, scheduleRepository.count());
        }
    }

    @Test
    public void createSchedule_whenAlreadyExists_shouldThrowConflict() {
        ScheduleCreateDto scheduleCreateDto = new ScheduleCreateDto();
        scheduleCreateDto.setActivityName("Baby football");
        scheduleCreateDto.setAgeMin(3);
        scheduleCreateDto.setAgeMax(5);
        scheduleCreateDto.setAssociationId(association.getId());
        scheduleCreateDto.setDayOfWeek(DayOfWeek.Lundi);
        scheduleCreateDto.setStartTime(LocalTime.of(15, 00));
        scheduleCreateDto.setEndTime(LocalTime.of(15, 30));

        assertThrows(ResourceAlreadyExistsException.class, () -> scheduleService.createSchedule(association.getId(), scheduleCreateDto));
    }

    @Test
    public void updateSchedule_whenSuccess() {
        ScheduleUpdateDto scheduleUpdateDto = new ScheduleUpdateDto();
        scheduleUpdateDto.setDayOfWeek(DayOfWeek.Vendredi);

        ScheduleReadDto updatedSchedule = scheduleService.updateSchedule(schedule.getId(), scheduleUpdateDto);

        assertEquals(scheduleUpdateDto.getDayOfWeek(), updatedSchedule.getDayOfWeek());
    }

    @Test
    public void updateSchedule_whenScheduleNotFound_shouldThrowNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> scheduleService.updateSchedule(999L, any(ScheduleUpdateDto.class)));
    }

    @Test
    public void deleteSchedule_whenSuccess(){
        scheduleService.deleteSchedule(schedule.getId());
        assertEquals(0, scheduleRepository.count());
    }






}
