package com.infoasso.api.service;

import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.dto.user.UserCreateDto;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    UserReadDto findUserById(Long id);

    UserReadDto createUser(UserCreateDto user);
}
