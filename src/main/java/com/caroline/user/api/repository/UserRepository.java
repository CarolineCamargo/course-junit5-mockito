package com.caroline.user.api.repository;

import com.caroline.user.api.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmailAndIdNot (String email, Integer id);
}