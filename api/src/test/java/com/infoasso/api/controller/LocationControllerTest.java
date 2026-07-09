package com.infoasso.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoasso.api.dto.location.LocationCreateDto;
import com.infoasso.api.dto.location.LocationReadDto;
import com.infoasso.api.dto.location.LocationUpdateDto;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.security.SecurityConfig;
import com.infoasso.api.security.jwt.AuthEntryPointJwt;
import com.infoasso.api.security.jwt.JwtUtils;
import com.infoasso.api.security.services.UserDetailsServiceImpl;
import com.infoasso.api.service.ILocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
public class LocationControllerTest {


    @Autowired
    private LocationController locationController;
    @MockitoBean
    private ILocationService locationService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private LocationReadDto location;


    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;


    @BeforeEach
    public void setUp() {
        location = new LocationReadDto(
                1L,
                "Gymnase",
                "12 rue paradis",
                "Lille",
                "59000"
        );
    }

    @Test
    @WithMockUser
    public void findAll_WhenSuccess() throws Exception {
        when(locationService.findAllLocation()).thenReturn(List.of(location));

        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    public void findById_WhenSuccess() throws Exception {
        when(locationService.findById(1L)).thenReturn(location);

        mockMvc.perform(get("/api/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gymnase"));
    }

    @Test
    @WithMockUser
    public void findById_WhenNotFound() throws Exception {
        when(locationService.findById(any(Long.class))).thenThrow(new ResourceNotFoundException("Location", any(Long.class)));

        mockMvc.perform(get("/api/locations/12"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void create_WhenSuccess() throws Exception {
        LocationReadDto newLoc = new LocationReadDto(2L, "Stade", "1 rue principale", "Lille", "59000");
        String jsonLocation = objectMapper.writeValueAsString(newLoc);

        when(locationService.findOrCreateLocation(any(LocationCreateDto.class))).thenReturn(newLoc);

        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLocation)
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    public void create_WhenNotAuthorize() throws Exception {
        LocationReadDto newLoc = new LocationReadDto(2L, "Stade", "1 rue principale", "Lille", "59000");
        String jsonLocation = objectMapper.writeValueAsString(newLoc);

        when(locationService.findOrCreateLocation(any(LocationCreateDto.class))).thenReturn(newLoc);

        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLocation)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void create_whenRequestBodyNotValid() throws Exception {
        LocationCreateDto newLoc = new LocationCreateDto("Stade", "1 rue principale", "", "59000");
        String jsonLocation = objectMapper.writeValueAsString(newLoc);

        when(locationService.findOrCreateLocation(any(LocationCreateDto.class))).thenReturn(location);

        mockMvc.perform(post("/api/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonLocation)
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void update_WhenSuccess() throws Exception {
        LocationUpdateDto updateLoc = new LocationUpdateDto("", "", "", "59800");
        String jsonUpdate = objectMapper.writeValueAsString(updateLoc);

        LocationReadDto updatedLoc = new LocationReadDto(1L, "Gymnase",
                "12 rue paradis",
                "Lille",
                "59800" );

        when(locationService.updateLocation(any(Long.class), any(LocationUpdateDto.class))).thenReturn(updatedLoc);

 mockMvc.perform(put("/api/locations/1")
         .contentType(MediaType.APPLICATION_JSON)
         .content(jsonUpdate)
         .with(csrf()))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.zipCode").value("59800"));
    }

}