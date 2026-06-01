package com.infoasso.api.service.impl;

import com.infoasso.api.dto.auth.JwtResponseDto;
import com.infoasso.api.dto.auth.LoginRequestDto;
import com.infoasso.api.dto.user.UserCreateDto;
import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.exceptions.BadRequestException;
import com.infoasso.api.model.Role;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
import com.infoasso.api.security.jwt.JwtUtils;
import com.infoasso.api.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final static Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public UserReadDto createUser(UserCreateDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Un utilisateur avec cet email existe déjà.");
        }

        logger.info("Creating user {}", user.getEmail());
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRole(Role.ROLE_USER);
        newUser.setGdprConsent(user.isGdprConsent());

        User savedUser = userRepository.save(newUser);
        logger.info("User created {}", savedUser.getEmail());
        return mapToUserDTO(savedUser);

    }

    @Override
    public JwtResponseDto login(LoginRequestDto loginRequest) {
        logger.info("Login request for {}", loginRequest.getEmail());

        // 1. AuthenticationManager verifie Email/Password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        // 2. Création du SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Création du token JWT
        String token = jwtUtils.generateToken(authentication);

        // 4. Récupération de l'entité User
        User user = (User) authentication.getPrincipal();

        // Récupération du rôtel sous forme de String
        String roleName = user.getRole().name();

        return new JwtResponseDto(
                token,
                user.getId(),
                user.getEmail(),
                roleName
        );
    }

    private UserReadDto mapToUserDTO(User user) {
        UserReadDto userDTO = new UserReadDto();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().name());
        return userDTO;
    }

}
