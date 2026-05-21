package com.infoasso.api.service.impl;

import com.infoasso.api.dto.user.UserCreateDto;
import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.exceptions.BadRequestException;
import com.infoasso.api.model.Role;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
import com.infoasso.api.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final static Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

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

    private UserReadDto mapToUserDTO(User user) {
        UserReadDto userDTO = new UserReadDto();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().name());
        return userDTO;
    }

}
