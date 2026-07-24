package com.infoasso.api.service.impl;

import com.infoasso.api.dto.user.UserReadDto;
import com.infoasso.api.dto.user.UserUpdateDto;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.User;
import com.infoasso.api.repository.UserRepository;
import com.infoasso.api.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserReadDto findUserById(Long id) {
        logger.info("Finding user by id {}", id);
        // 1. Récupérer l'utilisateur connecté depuis le contexte de sécurité
        User currentUser = getAuthenticatedUserOrThrow();

        // 2. Vérifier s'il essaie d'accéder à son propre profil (ou ADMIN)!!
        checkUserAccess(currentUser, id);

        // 3. Logique métier si tout est ok
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
        logger.info("User found {}", user.getEmail());
        return mapToUserDTO(user);
    }

    @Override
    public UserReadDto updateUser(Long id, UserUpdateDto updateDto) {
        logger.info("Trying to update user {}", id);
        // 1. Récupérer l'utilisateur connecté depuis le contexte de sécurité
        User currentUser = getAuthenticatedUserOrThrow();

        // 2. Vérifier s'il essaie d'accéder a son propre profil (ou ADMIN) !!
        checkUserAccess(currentUser, id);

        // 3. Logique métier si tout est ok
        User updatedUser = updateEntityFromDto(currentUser, updateDto);
        User savedUser = userRepository.save(updatedUser);

        return mapToUserDTO(savedUser);
    }

    private User getAuthenticatedUserOrThrow() {
        String currentPrincipalEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(currentPrincipalEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", 0L));
    }

    private void checkUserAccess(User currentUser, Long targetId) {
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        if (!currentUser.getId().equals(targetId) && !isAdmin) {
            throw new AccessDeniedException("Access denied");
        }
    }
    private User updateEntityFromDto(User user, UserUpdateDto dto) {
        // 1. Pour email
        if(dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        // 2. Pour password
        if(dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {

            //Sécurité : ancien mot de passe obligatoire pour modif
            if(dto.getOldPassword() == null || dto.getOldPassword().isBlank()){
                throw new IllegalArgumentException("L'ancien mot de passe est requis.");
            }

            if(!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                throw new IllegalArgumentException("L'ancien mot de passe est incorrect.");
            }

            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }
        return user;
    }

    private UserReadDto mapToUserDTO(User user) {
        UserReadDto userDTO = new UserReadDto();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().name());
        return userDTO;
    }

    private User mapUpdateDtoToUser(UserUpdateDto userUpdateDto) {
        User user = new User();
        user.setEmail(userUpdateDto.getEmail());
        user.setPassword(userUpdateDto.getNewPassword());
        return user;
    }
}