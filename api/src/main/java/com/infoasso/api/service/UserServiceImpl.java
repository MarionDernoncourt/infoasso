package com.infoasso.api.service;

import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.dto.user.UserCreateDto;
import com.infoasso.api.exceptions.BadRequestException;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.Role;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserReadDto findUserById(Long id) {
        logger.info("Finding user by id {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("User", id));
        logger.info("User found {}", user.getEmail());
        return mapToUserDTO(user);
    }

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

        User savedUser = userRepository.save(newUser);
        logger.info("User created {}", savedUser.getEmail());
        return mapToUserDTO(savedUser);

    }


    private UserReadDto mapToUserDTO(User user) {
        UserReadDto userDTO = new UserReadDto();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    private User mapToUser(UserReadDto userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}
