package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.entity.User;
import com.group1.proyect.freshbasket.dto.request.UserRequestDTO;
import com.group1.proyect.freshbasket.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService extends GenericService<User, UserRequestDTO, UserResponseDTO, Long> {

    List<UserResponseDTO> searchUsersByName(String name);

    UserResponseDTO getUserProfileByEmail(String email);

    UserResponseDTO updateUserProfileByEmail(String email, UserRequestDTO requestDTO);
}