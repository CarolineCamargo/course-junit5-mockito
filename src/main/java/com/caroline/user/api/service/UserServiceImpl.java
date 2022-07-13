package com.caroline.user.api.service;

import com.caroline.user.api.exception.DataIntegrityViolationException;
import com.caroline.user.api.exception.NotFoundException;
import com.caroline.user.api.model.DTO.UserDTO;
import com.caroline.user.api.model.entity.User;
import com.caroline.user.api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository repository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public User findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User create(UserDTO userDTO) {
        findByEmail(userDTO);
        return repository.save(mapper.map(userDTO, User.class));
    }

    @Override
    public User update(UserDTO userDTO) {
        findByEmail(userDTO);
        return repository.save(mapper.map(userDTO, User.class));
    }

    private void findByEmail(UserDTO userDTO){

        Optional<User> user = repository.findByEmail(userDTO.getEmail());

        if (user.isPresent() && !(user.get().getId().equals(userDTO.getId())))
            throw new DataIntegrityViolationException("Email already registered");
    }
}
