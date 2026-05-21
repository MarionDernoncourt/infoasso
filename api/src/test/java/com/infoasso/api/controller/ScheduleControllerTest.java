package com.infoasso.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoasso.api.dto.association.AssociationSummaryDto;
import com.infoasso.api.dto.schedule.ScheduleCreateDto;
import com.infoasso.api.dto.schedule.ScheduleReadDto;
import com.infoasso.api.dto.schedule.ScheduleUpdateDto;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.DayOfWeek;
import com.infoasso.api.service.impl.ScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.time.LocalTime;
import java.util.List;

@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    @Autowired
    private ScheduleController scheduleController;
    @MockitoBean
    private ScheduleServiceImpl scheduleService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private ScheduleReadDto schedule;
    private AssociationSummaryDto association;


    @BeforeEach
    public void setup() {
        association = new AssociationSummaryDto();
        association.setId(1L);
        association.setDisplayName("Football");

        schedule = new ScheduleReadDto();
        schedule.setId(1L);
        schedule.setActivityName("Baby football");
        schedule.setAgeMin(3);
        schedule.setAgeMax(5);
        schedule.setDayOfWeek(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.of(15, 00));
        schedule.setEndTime(LocalTime.of(15, 30));
        schedule.setAssociation(association);
    }

    @Test
    @WithMockUser
    public void findAll_noParams_whenSuccess() throws Exception {
        when(scheduleService.findAll(any(Long.class), isNull(), isNull(), isNull())).thenReturn(List.of(schedule));

        mockMvc.perform(get("/api/associations/1/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].activityName").value("Baby football"));
    }

    @Test
    @WithMockUser
    public void findAll_allParams_whenSuccess() throws Exception {
        when(scheduleService.findAll(any(Long.class), any(Integer.class), any(DayOfWeek.class), any(LocalTime.class))).thenReturn(List.of(schedule));

        mockMvc.perform(get("/api/associations/1/schedules")
                        .param("age", "4")
                        .param("dayOfWeek", "MONDAY")
                        .param("startTime", LocalTime.of(15, 0).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"));
    }

    @Test
    @WithMockUser
    public void findAll_whenAssociationNotFound() throws Exception {
        when(scheduleService.findAll(any(Long.class), any(Integer.class), any(DayOfWeek.class), any(LocalTime.class))).thenThrow(new RessourceNotFoundException("Association", 1L));

        mockMvc.perform(get("/api/associations/1/schedules")
                        .param("age", "4")
                        .param("dayOfWeek", "MONDAY")
                        .param("startTime", LocalTime.of(15, 0).toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser
    public void findAll_whenNoResults_shouldReturnEmptyList() throws Exception {
        when(scheduleService.findAll(anyLong(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/associations/1/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    public void findAll_withInvalidDay_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/associations/1/schedules")
                        .param("dayOfWeek", "JOUR_IMAGINAIRE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void createSchedule_whenSuccess() throws Exception {
        ScheduleCreateDto scheduleCreateDto = new ScheduleCreateDto();
        scheduleCreateDto.setActivityName("Baby football");
        scheduleCreateDto.setAgeMin(3);
        scheduleCreateDto.setAgeMax(5);
        scheduleCreateDto.setDayOfWeek(DayOfWeek.MONDAY);
        scheduleCreateDto.setStartTime(LocalTime.of(15, 00));
        scheduleCreateDto.setEndTime(LocalTime.of(15, 30));
        scheduleCreateDto.setAssociationId(association.getId());

        String json = objectMapper.writeValueAsString(scheduleCreateDto);

        when(scheduleService.createSchedule(any(Long.class), any(ScheduleCreateDto.class))).thenReturn(schedule);

        mockMvc.perform(post("/api/associations/1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void createSchedule_whenArgumentNotValid_ShouldThrowBadRequest() throws Exception {
        ScheduleCreateDto scheduleCreateDto = new ScheduleCreateDto();
        scheduleCreateDto.setActivityName("Baby football");
        scheduleCreateDto.setAgeMin(5);
        scheduleCreateDto.setAgeMax(3);
        scheduleCreateDto.setDayOfWeek(DayOfWeek.MONDAY);
        scheduleCreateDto.setStartTime(LocalTime.of(15, 00));
        scheduleCreateDto.setEndTime(LocalTime.of(15, 00));
        scheduleCreateDto.setAssociationId(association.getId());

        String json = objectMapper.writeValueAsString(scheduleCreateDto);

        when(scheduleService.createSchedule(any(Long.class), any(ScheduleCreateDto.class))).thenReturn(schedule);

        mockMvc.perform(post("/api/associations/1/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser
    public void updateSchedule_whenSuccess() throws Exception {
        ScheduleUpdateDto scheduleUpdateDto = new ScheduleUpdateDto();
        scheduleUpdateDto.setEndTime(LocalTime.of(16, 30));

        String json = objectMapper.writeValueAsString(scheduleUpdateDto);

        when(scheduleService.updateSchedule(any(Long.class), any(ScheduleUpdateDto.class))).thenReturn(schedule);
        Long scheduleId = schedule.getId();

        mockMvc.perform(put("/api/associations/1/schedules/" + scheduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void updateSchedule_whenArgumentNotValid_ShouldThrowBadRequest() throws Exception {
        String jsonInvalide = """
                {
                    "activityName": "Foot",
                    "dayOfWeek": "JOUR_IMAGINAIRE"
                }
                """;

        // 2. On exécute la requête
        mockMvc.perform(put("/api/associations/1/schedules/" + schedule.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalide)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void updateSchedule_whenAgeNotRangeValid() throws Exception {
        ScheduleUpdateDto scheduleUpdateDto = new ScheduleUpdateDto();
        scheduleUpdateDto.setAgeMin(10);
        scheduleUpdateDto.setAgeMax(8);

        String json = objectMapper.writeValueAsString(scheduleUpdateDto);

        when(scheduleService.updateSchedule(any(Long.class), any(ScheduleUpdateDto.class))).thenReturn(schedule);
        Long scheduleId = schedule.getId();

        mockMvc.perform(put("/api/associations/1/schedules/" + scheduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void deleteSchedule_whenSuccess() throws Exception {
        doNothing().when(scheduleService).deleteSchedule(any(Long.class));

        mockMvc.perform(delete("/api/associations/1/schedules/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void deleteSchedule_whenScheduleNotFound() throws Exception {
        doThrow(new RessourceNotFoundException("Schedule", 999L)).when(scheduleService).deleteSchedule(999L);

        mockMvc.perform(delete("/api/associations/1/schedules/999").with(csrf()))
                .andExpect(status().isNotFound());
    }

}
