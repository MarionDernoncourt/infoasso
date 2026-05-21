package com.infoasso.api.service;

import com.infoasso.api.dto.user.UserCreateDto;
import com.infoasso.api.dto.user.UserReadDto;
import org.springframework.stereotype.Service;

@Service
public interface IAuthService {

    UserReadDto createUser(UserCreateDto user);

}
