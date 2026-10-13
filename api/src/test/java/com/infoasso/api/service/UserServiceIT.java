package com.infoasso.api.service;


import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.dto.user.UserUpdateDto;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.Role;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
import com.infoasso.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIT {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        user = new User();
        user.setEmail("test@asso.com");
        user.setPassword("Password123!");
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "test@asso.com", roles = {"USER"})
    public void findUserById_WhenSuccess() throws Exception {
        UserReadDto test = userService.findUserById(user.getId());
        assertEquals(user.getEmail(), test.getEmail());
    }

    @Test
    @WithMockUser(username = "test@asso.com", roles = {"USER"})
    public void findUserById_WhenNotFound() throws Exception {
        userRepository.deleteAll();
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.findUserById(user.getId());
        });
    }

    @Test
    @WithMockUser(username = "test@asso.com", roles = {"USER"})
    public void updateUser_whenSuccess() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("updated@asso.com");
        UserReadDto updatedUser = userService.updateUser(user.getId(), updateDto);
        assertEquals("updated@asso.com", updatedUser.getEmail());
    }

    @Test
    @WithMockUser(username = "test@asso.com", roles = {"USER"})
    public void updateUser_whenNotFound() throws Exception {
        userRepository.deleteAll();
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(user.getId(), new UserUpdateDto());
        });
    }

    @Test
    @WithMockUser(username = "test@asso.com", roles = {"USER"})
    public void getMyProfile_whenSuccess() throws Exception {
        UserReadDto profile = userService.getMyProfile();
        assertEquals(user.getEmail(), profile.getEmail());
    }

    @Test
    @WithMockUser(username = "test@asso.com", roles = {"USER"})
    public void getMyProfile_whenNotFound() throws Exception {
        userRepository.deleteAll();
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getMyProfile();
        });
    }

    @Test
    @WithMockUser(username = "test@asso.com", roles = {"USER"})
    public void updateUser_whenPasswordUpdateSuccess() throws Exception {
        user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("OldPassword123!"));
        userRepository.save(user);

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setOldPassword("OldPassword123!");
        updateDto.setNewPassword("NewPassword123!");

        UserReadDto updatedUser = userService.updateUser(user.getId(), updateDto);
        assertEquals(user.getEmail(), updatedUser.getEmail());
    }

    @Test
    @WithMockUser(username = "test@asso.com", roles = {"USER"})
    public void updateUser_whenWrongOldPassword_ThrowsException() {
        user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("OldPassword123!"));
        userRepository.save(user);

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setOldPassword("WrongPassword");
        updateDto.setNewPassword("NewPassword123!");

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(user.getId(), updateDto);
        });
    }

}