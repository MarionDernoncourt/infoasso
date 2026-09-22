package com.infoasso.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoasso.api.dto.association.AssociationCreateDto;
import com.infoasso.api.dto.association.AssociationReadDto;
import com.infoasso.api.dto.association.AssociationUpdateDto;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.CategoryType;
import com.infoasso.api.security.jwt.AuthEntryPointJwt;
import com.infoasso.api.security.jwt.JwtUtils;
import com.infoasso.api.security.services.UserDetailsServiceImpl;
import com.infoasso.api.service.IAssociationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssociationController.class)
public class AssociationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAssociationService associationService;

    // --- Mocks indispensables pour charger le contexte Security ---
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    private AssociationReadDto association;

    @BeforeEach
    void setup() {
        association = new AssociationReadDto();
        association.setId(1L);
        association.setRnaNumber("W00001546");
        association.setOfficialName("OFFICIAL TEST ASSO");
        association.setDisplayName("Mon Asso de Test");
        association.setDescription("Une super description");
        association.setEmail("email@asso.com");
        association.setCategoryLabel("SPORT");
        association.setOwnerEmail("owner@test.com");
    }

    @Test
    @WithMockUser // Accès public en GET selon ta config
    public void findAll_shouldReturn_200OK() throws Exception {
        when(associationService.findAll(null, null, null, null)).thenReturn(List.of(association));

        mockMvc.perform(get("/api/associations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].displayName").value("Mon Asso de Test"));
    }

    @Test
    @WithMockUser
    public void findById_shouldReturn_200OK() throws Exception {
        when(associationService.findById(1L)).thenReturn(association);

        mockMvc.perform(get("/api/associations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.displayName").value("Mon Asso de Test"));
    }

    @Test
    @WithMockUser
    public void findById_shouldReturn_404NotFound() throws Exception {
        when(associationService.findById(99L)).thenThrow(new ResourceNotFoundException("Association", 99L));

        mockMvc.perform(get("/api/associations/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createAssociation_shouldReturn_201Created() throws Exception {
        AssociationCreateDto createDto = new AssociationCreateDto();
        createDto.setDisplayName("Nouveau Nom"); // Champ du CreateDto
        createDto.setRnaNumber("W123456789");
        createDto.setEmail("test@asso.fr");
        createDto.setCategoryLabel("HOCKEY");
        createDto.setCategoryType(CategoryType.SPORT);
        createDto.setDescription("Ceci est une description valide et complète.");
        createDto.setCity("Loos");

        when(associationService.createAssociation(any(AssociationCreateDto.class))).thenReturn(association);

        mockMvc.perform(post("/api/associations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .with(csrf())) // CSRF requis avec Spring Security
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "user@test.fr", roles = "USER")
    public void updateAssociation_shouldReturn_200OK() throws Exception {
        AssociationUpdateDto updateDto = new AssociationUpdateDto();
        updateDto.setDisplayName("Nom Modifié");
        updateDto.setEmail("modifie@test.fr");
        updateDto.setCategoryLabel("HOCKEY");
        updateDto.setCategoryType(CategoryType.SPORT);

        AssociationReadDto updatedResponse = new AssociationReadDto();
        updatedResponse.setId(1L);
        updatedResponse.setDisplayName("Nom Modifié");

        when(associationService.updateAssociation(eq(1L), any(AssociationUpdateDto.class), eq("user@test.fr"))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/associations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Nom Modifié"));
    }

    @Test
    @WithMockUser(username = "user@test.fr", roles = "USER")
    public void deleteAssociation_shouldReturn_204NoContent() throws Exception {
        // On met eq(1L) partout pour que Mockito soit content
        doNothing().when(associationService).deleteAssociation(eq(1L), eq("user@test.fr"));

        mockMvc.perform(delete("/api/associations/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@test.fr", roles = "USER")
    public void deleteAssociation_shouldReturn_404NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Association", 10L))
                .when(associationService).deleteAssociation(eq(10L), eq("user@test.fr"));

        mockMvc.perform(delete("/api/associations/10")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}