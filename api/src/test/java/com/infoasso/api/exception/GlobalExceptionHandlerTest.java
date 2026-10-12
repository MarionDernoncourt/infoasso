package com.infoasso.api.exception;

import com.infoasso.api.controller.AuthController;
import com.infoasso.api.controller.UserController;
import com.infoasso.api.exceptions.GlobalExceptionHandler;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.service.IAssociationService;
import com.infoasso.api.service.IAuthService;
import com.infoasso.api.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {AuthController.class, UserController.class, GlobalExceptionHandler.class})
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private IAssociationService associationService;
    @MockitoBean
    private IUserService userService;
    @MockitoBean
    private IAuthService authService;

    @WithMockUser(username = "user@test.com", roles = {"USER"})
    @Test
    void shouldHandleResourceNotFoundException() throws Exception {
        given(userService.findUserById(3L))
                .willThrow(new ResourceNotFoundException("User", 3L));

        mockMvc.perform(get("/api/users/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User avec l'id 3 n'existe pas"));
    }

    @WithMockUser(username = "user@test.com", roles = {"USER"})
    @Test
    void shouldHandleAccessDeniedException() throws Exception {

        given(userService.getMyProfile())
                .willThrow(new AccessDeniedException("Accès refusé"));

        mockMvc.perform(get("/api/users/myprofile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"USER"})
    void shouldHandleValidationException() throws Exception {
        String invalidUserJson = "{" +
                "\"email\": \"\"" +
                ", \"password\": \"Password123!\"" +
                "}";

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @WithMockUser(username = "user@test.com", roles = {"USER"})
    @Test
    void shouldHandleGenericInternalServerError() throws Exception {
        given(userService.getMyProfile())
                .willThrow(new RuntimeException("Erreur critique inattendue"));

        mockMvc.perform(get("/api/users/myprofile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // 500
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").exists());
    }
}
