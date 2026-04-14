package com.infoasso.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoasso.api.dto.category.CategoryCreateDto;
import com.infoasso.api.dto.category.CategoryReadDto;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.CategoryType;
import com.infoasso.api.service.ICategoryService;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private CategoryController categoryController;
    @MockitoBean
    private ICategoryService categoryService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private CategoryReadDto category;

    @BeforeEach
    public void setup() {
        category = new CategoryReadDto();
        category.setId(1L);
        category.setLabel("Category 1");
        category.setType(CategoryType.SPORT);

    }

    @Test
    @WithMockUser
    public void findAll_whenSuccess() throws Exception {
        when(categoryService.findAll()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

    }

    @Test
    @WithMockUser
    public void findAll_whenInternalError() throws Exception {
        when(categoryService.findAll()).thenThrow(new RuntimeException("Internal error"));

        mockMvc.perform(get("/api/category"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser
    public void findById_whenSuccess() throws Exception {
        when(categoryService.findById(any(Long.class))).thenReturn(category);

        mockMvc.perform(get("/api/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Category 1"));
    }

    @Test
    @WithMockUser
    public void findById_shouldReturn404NotFound() throws Exception {
        when(categoryService.findById(any(Long.class))).thenThrow(new RessourceNotFoundException("Categorie non trouvée", 3L));
        mockMvc.perform(get("/api/category/3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser
    public void createCategory_whenSuccess() throws Exception {
        CategoryCreateDto categoryCreateDto = new CategoryCreateDto();
        categoryCreateDto.setLabel("Catégory 1");
        categoryCreateDto.setType(CategoryType.SPORT);

        when(categoryService.createCategory(any(CategoryCreateDto.class))).thenReturn(category);

        String jsonCategory = objectMapper.writeValueAsString(categoryCreateDto);

        mockMvc.perform(post("/api/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCategory)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void createCategory_whenArgumentNotValid() throws Exception {
        CategoryCreateDto categoryCreateDto = new CategoryCreateDto();
        categoryCreateDto.setLabel("");
        categoryCreateDto.setType(CategoryType.SPORT);

        when(categoryService.createCategory(any(CategoryCreateDto.class))).thenReturn(category);

        String jsonCategory = objectMapper.writeValueAsString(categoryCreateDto);

        mockMvc.perform(post("/api/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCategory)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

}