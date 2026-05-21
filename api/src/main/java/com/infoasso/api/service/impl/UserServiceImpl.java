package com.infoasso.api.service.impl;

import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
import com.infoasso.api.service.IUserService;
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
    public UserReadDto findUserById(Long id) {
        logger.info("Finding user by id {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("User", id));
        logger.info("User found {}", user.getEmail());
        return mapToUserDTO(user);
    }

    private UserReadDto mapToUserDTO(User user) {
        UserReadDto userDTO = new UserReadDto();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().name());
        return userDTO;
    }

}
