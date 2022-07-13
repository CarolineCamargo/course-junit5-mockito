package com.caroline.user.api.service;

import com.caroline.user.api.model.DTO.UserDTO;
import com.caroline.user.api.model.entity.User;

import java.util.List;

public interface UserService{

    User findById(Integer id);

    List<User> findAll();

    User create(UserDTO userDTO);

    User update(UserDTO userDTO);
}
