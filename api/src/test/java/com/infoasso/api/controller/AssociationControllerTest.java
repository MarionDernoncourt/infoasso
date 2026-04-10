package com.infoasso.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoasso.api.dto.association.AssociationCreateDto;
import com.infoasso.api.dto.association.AssociationReadDto;
import com.infoasso.api.dto.association.AssociationUpdateDto;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.CategoryType;
import com.infoasso.api.service.IAssociationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssociationController.class)
public class AssociationControllerTest {

    @Autowired
    private AssociationController associationController;
    @MockitoBean
    private IAssociationService associationService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private AssociationReadDto association;

    @BeforeEach
    void setup() {
        association = new AssociationReadDto();

        association.setId(1L);
        association.setName("test");
        association.setDescription("description association");
        association.setEmail("email@asso.com");
        association.setCategoryLabel("SPORT");
        association.setOwnerEmail("email@asso.com");
    }


    @Test
    @WithMockUser
    public void findAll_withNoParams_shouldReturn_200OK() throws Exception {
        List<AssociationReadDto> associations = List.of(association);
        when(associationService.findAll(null, null)).thenReturn(associations);

        mockMvc.perform(get("/api/associations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    public void findAll_withParams_shouldReturn_200OK() throws Exception {
        List<AssociationReadDto> associations = List.of(association);
        when(associationService.findAll("test", "sport")).thenReturn(associations);

        mockMvc.perform(get("/api/associations")
                        .param("name", "test")
                        .param("category", "sport"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    public void findById_shouldReturn_200OK() throws Exception {
        when(associationService.findById(any(Long.class))).thenReturn(association);

        mockMvc.perform(get("/api/associations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    @WithMockUser
    public void findById_shouldReturn_404NotFound() throws Exception {
        when(associationService.findById(any(Long.class))).thenThrow(new RessourceNotFoundException("Association", 12L));

        mockMvc.perform(get("/api/associations/12"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser
    public void createAssociation_shouldReturn_201Created() throws Exception {
        AssociationCreateDto associationCreate = new AssociationCreateDto();
        associationCreate.setName("test");
        associationCreate.setDescription("description association sportive");
        associationCreate.setEmail("asso@info.com");
        associationCreate.setCategoryLabel("hockey");
        associationCreate.setCategoryType(CategoryType.SPORT);

        String associationJson = objectMapper.writeValueAsString(associationCreate);

        when(associationService.createAssociation(any(AssociationCreateDto.class))).thenReturn(association);

        mockMvc.perform(post("/api/associations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(associationJson)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    public void createAssociation_shouldReturn_400BadRequest() throws Exception {
        AssociationCreateDto associationCreate = new AssociationCreateDto();
        associationCreate.setName("test");
        associationCreate.setDescription("description association sportive");
        associationCreate.setEmail("asso");
        associationCreate.setCategoryLabel("hockey");
        associationCreate.setCategoryType(CategoryType.SPORT);

        String associationJson = objectMapper.writeValueAsString(associationCreate);

        mockMvc.perform(post("/api/associations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(associationJson)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void createAssociation_shouldReturn_500InternalError() throws Exception {
        AssociationCreateDto associationCreate = new AssociationCreateDto();
        associationCreate.setName("test");
        associationCreate.setDescription("description association sportive");
        associationCreate.setEmail("asso@info.com");
        associationCreate.setCategoryLabel("hockey");
        associationCreate.setCategoryType(CategoryType.SPORT);
        String associationJson = objectMapper.writeValueAsString(associationCreate);

        when(associationService.createAssociation(any(AssociationCreateDto.class))).thenThrow(new RuntimeException("Erreur interne"));

        mockMvc.perform(post("/api/associations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(associationJson)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser
    public void updateAssociation_shouldReturn_200OK() throws Exception {

        AssociationUpdateDto associationToUpdate = new AssociationUpdateDto();
        associationToUpdate.setName("association updated");
        associationToUpdate.setDescription("description association musicale");

        String associationJson = objectMapper.writeValueAsString(associationToUpdate);

        AssociationReadDto updatedResponse = new AssociationReadDto();
        updatedResponse.setId(1L);
        updatedResponse.setName("association updated");

        when(associationService.updateAssociation(any(Long.class), any(AssociationUpdateDto.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/associations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(associationJson)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("association updated"));

    }

    @Test
    @WithMockUser
    public  void updateAssociation_shouldReturn_400BadRequest() throws Exception {
        AssociationUpdateDto associationToUpdate = new AssociationUpdateDto();
        associationToUpdate.setName("association updated");
        associationToUpdate.setDescription("description association musicale");
        associationToUpdate.setEmail("asso");

        String associationJson = objectMapper.writeValueAsString(associationToUpdate);

        AssociationReadDto updatedResponse = new AssociationReadDto();
        updatedResponse.setId(1L);
        updatedResponse.setDescription("association sportive");

        when(associationService.updateAssociation(any(Long.class), any(AssociationUpdateDto.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/associations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(associationJson)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser
    public void deleteAssociation_shouldReturn_204NoContent() throws Exception {
       doNothing().when(associationService).deleteAssociation(any(Long.class));

       mockMvc.perform(delete("/api/associations/1")
                       .with(csrf()))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void deleteAssociation_shouldReturn_404NotFound() throws Exception {

    doThrow(new RessourceNotFoundException("Association", 10L)).when(associationService).deleteAssociation(any(Long.class));

    mockMvc.perform(delete("/api/associations/10").with(csrf()))
            .andExpect(status().isNotFound());
        verify(associationService, times(1)).deleteAssociation(10L);
    }
}
