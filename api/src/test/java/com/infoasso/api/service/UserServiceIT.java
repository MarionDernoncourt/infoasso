package com.infoasso.api.service;

import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.Role;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
import com.infoasso.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIT {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void getUserById_whenUserExist() {
        User user = new User();
        user.setEmail("info@asso.com");
        user.setPassword("Password123");
        user.setRole(Role.ROLE_USER);

        User savedUser = userRepository.save(user);
        Long generatedId = savedUser.getId();

        UserReadDto resultDTO = userService.findUserById(generatedId);

        assertEquals(generatedId, resultDTO.getId());
        assertEquals(user.getEmail(), resultDTO.getEmail());
    }

    @Test
    public void getUserById_whenUserDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.findUserById(254L);
        });
    }




}
