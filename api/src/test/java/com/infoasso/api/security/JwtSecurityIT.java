package com.infoasso.api.security;

import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
import com.infoasso.api.security.jwt.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class JwtSecurityIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Value("${infoasso.app.jwtSecret}") // Adapte selon le nom de ta propriété dans ton .env ou properties
    private String jwtSecret;

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
    @WithMockUser(username = "user@asso.com", roles = "USER")
    void shouldAuthenticateUserWithValidCredentials() throws Exception {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("user@asso.com")
                .password("Passsword123!")
                .roles("USER")
                .build();
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String token = jwtUtils.generateToken(auth);

        mockMvc.perform(get("/api/users/myprofile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedForInvalidToken() throws Exception {
        mockMvc.perform(get("/api/users/myprofile")
                .header("Authorization", "Bearer 123invalidtoken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedForMissingToken() throws Exception {
        mockMvc.perform(get("/api/users/myprofile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedForExpiredToken() throws Exception {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        // 2. Fabrication d'un token dont la date d'expiration est dépassée (ex: expiré il y a 1 heure)
        String expiredToken = Jwts.builder()
                .subject("user@asso.com")
                .issuedAt(new Date(System.currentTimeMillis() - 7200000L)) // Émis il y a 2h
                .expiration(new Date(System.currentTimeMillis() - 3600000L)) // Expiré il y a 1h
                .signWith(key)
                .compact();

        mockMvc.perform(get("/api/users/myprofile")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }
}
