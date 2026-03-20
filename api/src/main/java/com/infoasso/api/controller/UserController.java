package com.infoasso.api.controller;

import com.infoasso.api.dto.UserDTO;
import com.infoasso.api.dto.UserRegistrationDTO;
import com.infoasso.api.model.User;
import com.infoasso.api.service.IUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        logger.info("GET /api/user/{} : Request received for user : {}", id, id);
        UserDTO userDTO = userService.findUserById(id);
        logger.info(" Response received : 200 OK : The user {} is found", id);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserRegistrationDTO user) {
        logger.info("POST /api/user : Request received for user : {}",  user.getEmail());
        UserDTO newUser = userService.createUser(user);
        logger.info(" Response received : 201 CREATED : The user {} has been created", newUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}
