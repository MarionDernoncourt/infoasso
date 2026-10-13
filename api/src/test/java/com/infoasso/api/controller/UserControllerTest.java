package com.infoasso.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.security.jwt.AuthEntryPointJwt;
import com.infoasso.api.security.jwt.JwtUtils;
import com.infoasso.api.security.services.UserDetailsServiceImpl;
import com.infoasso.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserServiceImpl userService;
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;


    @Test
    @WithMockUser(roles = "USER")
    public void findUserById_shouldReturn_200Ok() throws Exception {
        UserReadDto user = new UserReadDto();
        user.setId(1L);
        user.setEmail("info@asso.com");

        when(userService.findUserById(any(Long.class))).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.email").value("info@asso.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findUserById_shouldReturn_500InternalServerError() throws Exception {
        when(userService.findUserById(any(Long.class))).thenThrow(new RuntimeException("Une erreur interne est survenue"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Une erreur interne est survenue"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findUserById_shouldReturn_404NotFound() throws Exception {
        when(userService.findUserById(any(Long.class))).thenThrow(new ResourceNotFoundException("User", 1L));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@asso.com", roles = {"USER"})
    public void getMyProfile_shouldReturn_200Ok() throws Exception {
        UserReadDto user = new UserReadDto();
        user.setId(1L);
        user.setEmail("test@asso.com");

        when(userService.getMyProfile()).thenReturn(user);

        mockMvc.perform(get("/api/users/myprofile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@asso.com"));
    }

    @Test
    @WithMockUser(username = "test@asso.com", roles = {"USER"})
    public void getMyProfile_shouldReturn_404NotFound() throws Exception {
        when(userService.getMyProfile()).thenThrow(new ResourceNotFoundException("User", 1L));

        mockMvc.perform(get("/api/users/myprofile"))
                .andExpect(status().isNotFound());
    }


}
