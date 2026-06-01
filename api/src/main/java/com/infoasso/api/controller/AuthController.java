package com.infoasso.api.controller;

import com.infoasso.api.dto.auth.JwtResponseDto;
import com.infoasso.api.dto.auth.LoginRequestDto;
import com.infoasso.api.dto.user.UserCreateDto;
import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.service.IAuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    private IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserReadDto> createUser(@Valid @RequestBody UserCreateDto user) {
        logger.info("POST /api/auth/register : Request received for user : {}",  user.getEmail());
        UserReadDto newUser = authService.createUser(user);
        logger.info(" Response received : 201 CREATED : The user {} has been created", newUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login (@Valid @RequestBody LoginRequestDto loginRequestDto) {
        logger.info("POST /api/auth/login : Request received for user : {}", loginRequestDto.getEmail());
        JwtResponseDto jwtResponseDto = authService.login(loginRequestDto);
        logger.info("Response received : 200 OK : The user {} is logged in", jwtResponseDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(jwtResponseDto);
    }

}
