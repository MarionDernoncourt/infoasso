package com.infoasso.api.security;

import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SecurityIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        if (!userRepository.existsByEmail("user@asso.com")) {
            User user = new User();
            user.setEmail("user@asso.com");
            user.setPassword("encoded_password");
            user.setRole(com.infoasso.api.model.Role.ROLE_USER);
            userRepository.save(user);
        }
    }

    @Test
    void shouldReturnUnauthorizedWhenAccessingProtectedEndpointWithoutAuth() throws Exception {
        // Un utilisateur non authentifié essaie d'accéder à une route protégée
        mockMvc.perform(get("/api/users/myprofile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@asso.com", roles = {"USER"})
    void shouldAllowAccessWhenUserWithRoleUserAccessesProtectedEndpoint() throws Exception {
        // Un responsable d'association authentifié (ROLE_USER) accède à sa route protégée
        mockMvc.perform(get("/api/users/myprofile"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalidOrUserMissing() throws Exception {
        // Simule un utilisateur dont le token est valide syntaxiquement mais non existant en base
        mockMvc.perform(get("/api/users/myprofile")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidtoken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAccessToPublicEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/associations"))
                .andExpect(status().isOk());
    }
}
