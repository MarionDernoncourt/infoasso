package com.infoasso.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoasso.api.dto.UserDTO;
import com.infoasso.api.dto.UserRegistrationDTO;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserServiceImpl userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void findUserById_shouldReturn_200Ok() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setEmail("info@asso.com");

        when(userService.findUserById(any(Long.class))).thenReturn(user);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.email").value("info@asso.com"));
    }

    @Test
    @WithMockUser
    public void findUserById_shouldReturn_500InternalServerError() throws Exception {
        when(userService.findUserById(any(Long.class))).thenThrow(new RuntimeException("Une erreur interne est survenue"));

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Une erreur interne est survenue"));
    }

    @Test
    @WithMockUser
    public void findUserById_shouldReturn_404NotFound() throws Exception {
        when(userService.findUserById(any(Long.class))).thenThrow(new RessourceNotFoundException("User", 1L));

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void createUser_shouldReturn_200Created() throws Exception {
        UserRegistrationDTO newUser = new UserRegistrationDTO();
        newUser.setEmail("user@asso.com");
        newUser.setPassword("Password123");

        String userJson = objectMapper.writeValueAsString(newUser);

        UserDTO savedUser = new UserDTO();
        savedUser.setId(1L);
        savedUser.setEmail("user@asso.com");

        when(userService.createUser(any(UserRegistrationDTO.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.email").value("user@asso.com"));

    }

    @Test
    @WithMockUser
    public void createUser_shouldReturn_400BadRequest() throws Exception {
        UserRegistrationDTO newUser = new UserRegistrationDTO();
        newUser.setEmail("user.com");
        newUser.setPassword("password123");

        String userJson = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

    }

    @Test
    @WithMockUser
    public void createUser_shouldReturn_500InternalServerError() throws Exception {
        UserRegistrationDTO newUser = new UserRegistrationDTO();
        newUser.setEmail("user@asso.com");
        newUser.setPassword("Password123");

        String userJson = objectMapper.writeValueAsString(newUser);

        when(userService.createUser(any(UserRegistrationDTO.class))).thenThrow(new RuntimeException("Une erreur interne est survenue"));

        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
                .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists());
    }
}
