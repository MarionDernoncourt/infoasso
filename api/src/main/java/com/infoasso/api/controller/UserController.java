package com.infoasso.api.controller;

import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.dto.user.UserUpdateDto;
import com.infoasso.api.service.IUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserReadDto> getUser(@PathVariable Long id) {
        logger.info("GET /api/users/{} : Request received for user : {}", id, id);
        UserReadDto userDTO = userService.findUserById(id);
        logger.info(" Response received : 200 OK : The user {} is found", id);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
        public ResponseEntity<UserReadDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        logger.info("PUT /api/users/{} : Request received for user : {}", id, id);
        UserReadDto userDTO = userService.updateUser(id, userUpdateDto);
        logger.info(" Response received : 200 OK : The user {} is updated", id);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
        }

}
