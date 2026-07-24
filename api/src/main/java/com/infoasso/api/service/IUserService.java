package com.infoasso.api.service;

import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.dto.user.UserCreateDto;
import com.infoasso.api.dto.user.UserUpdateDto;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    UserReadDto findUserById(Long id);

    UserReadDto updateUser(Long id, UserUpdateDto updateDto);
}
