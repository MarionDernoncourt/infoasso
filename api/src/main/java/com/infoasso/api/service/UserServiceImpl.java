package com.infoasso.api.service;

import com.infoasso.api.dto.UserDTO;
import com.infoasso.api.dto.UserRegistrationDTO;
import com.infoasso.api.exceptions.BadRequestException;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    public UserDTO findUserById(Long id) {
        logger.info("Finding user by id {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("User", id));
        logger.info("User found {}", user.getEmail());
        return mapToUserDTO(user);
    }

    @Override
    public UserDTO createUser(UserRegistrationDTO user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Un utilisateur avec cet email existe déjà.");
        }

        logger.info("Creating user {}", user.getEmail());
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(newUser);
        logger.info("User created {}", savedUser.getEmail());
        return mapToUserDTO(savedUser);

    }


    private UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    private User mapToUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}
