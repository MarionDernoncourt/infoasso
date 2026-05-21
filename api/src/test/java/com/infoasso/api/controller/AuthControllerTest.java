package com.infoasso.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoasso.api.dto.user.UserCreateDto;
import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.security.jwt.AuthEntryPointJwt;
import com.infoasso.api.security.jwt.JwtUtils;
import com.infoasso.api.security.services.UserDetailsServiceImpl;
import com.infoasso.api.service.impl.AuthServiceImpl;
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

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private AuthController authController;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthServiceImpl authService;
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    @WithMockUser
    public void createUser_shouldReturn_200Created() throws Exception {
        UserCreateDto newUser = new UserCreateDto();
        newUser.setEmail("user@asso.com");
        newUser.setPassword("Password123");
        newUser.setGdprConsent(true);

        String userJson = objectMapper.writeValueAsString(newUser);

        UserReadDto savedUser = new UserReadDto();
        savedUser.setId(1L);
        savedUser.setEmail("user@asso.com");

        when(authService.createUser(any(UserCreateDto.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("user@asso.com"));

    }

    @Test
    @WithMockUser
    public void createUser_shouldReturn_400BadRequest() throws Exception {
        UserCreateDto newUser = new UserCreateDto();
        newUser.setEmail("user.com");
        newUser.setPassword("password123");
        newUser.setGdprConsent(true);

        String userJson = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

    }

    @Test
    @WithMockUser
    public void createUser_shouldReturn_500InternalServerError() throws Exception {
        UserCreateDto newUser = new UserCreateDto();
        newUser.setEmail("user@asso.com");
        newUser.setPassword("Password123");
        newUser.setGdprConsent(true);

        String userJson = objectMapper.writeValueAsString(newUser);

        when(authService.createUser(any(UserCreateDto.class))).thenThrow(new RuntimeException("Une erreur interne est survenue"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists());
    }

}
