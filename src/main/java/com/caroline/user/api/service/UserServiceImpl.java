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
        userDTO.setId(0); //deveria validar se o Id já não existe, fiz isso para não ficar muito diferente do instrutor
        validateExistsByEmail(userDTO);
        return repository.save(mapper.map(userDTO, User.class));
    }

    @Override
    public User update(UserDTO userDTO) {
        validateExistsByEmail(userDTO);
        return repository.save(mapper.map(userDTO, User.class));
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        repository.deleteById(id);
    }

    private void validateExistsByEmail(UserDTO userDTO){
        if (repository.existsByEmailAndIdNot(userDTO.getEmail(), userDTO.getId()))
            throw new DataIntegrityViolationException("Email already registered");
    }
}
