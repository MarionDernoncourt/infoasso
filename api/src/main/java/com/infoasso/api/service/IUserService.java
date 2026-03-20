package com.infoasso.api.service;

import com.infoasso.api.dto.UserDTO;
import com.infoasso.api.dto.UserRegistrationDTO;
import com.infoasso.api.model.User;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    UserDTO findUserById(Long id);

    UserDTO createUser(UserRegistrationDTO user);
}
