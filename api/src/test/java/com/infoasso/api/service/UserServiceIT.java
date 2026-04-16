package com.infoasso.api.service;

import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.dto.user.UserCreateDto;
import com.infoasso.api.exceptions.BadRequestException;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.Role;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
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
        assertThrows(RessourceNotFoundException.class, () -> {
            userService.findUserById(254L);
        });
    }

    @Test
    public void createUser_withSucces() {
        UserCreateDto userRegistration = new UserCreateDto();
        userRegistration.setEmail("user@asso.com");
        userRegistration.setPassword("Password123");

        UserReadDto savedUser = userService.createUser(userRegistration);
        User userInDb = userRepository.findById(savedUser.getId()).orElseThrow();

        assertEquals(userRegistration.getEmail(), savedUser.getEmail());
        assertNotNull(savedUser.getId());
        assertTrue(userInDb.getPassword().startsWith("$2a$"));
    }

    @Test
    public void createUser_whenUserAlreadyExists() {
        User user = new User();
        user.setEmail("user@asso.com");
        user.setPassword("Passwordzz1");
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);

        UserCreateDto userRegistration = new UserCreateDto();
        userRegistration.setEmail("user@asso.com");
        userRegistration.setPassword("Password123");

        assertThrows(BadRequestException.class, () -> {
            userService.createUser(userRegistration);
        });
    }


}
