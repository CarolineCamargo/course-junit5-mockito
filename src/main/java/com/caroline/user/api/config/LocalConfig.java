package com.caroline.user.api.config;

import com.caroline.user.api.model.entity.User;
import com.caroline.user.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("local")
public class LocalConfig {

    @Autowired
    private UserRepository repository;

    @Bean
    public void startDB(){

        User u1 = User.builder().id(null).name("Valdir").email("valdir@email.com").password("123").build();
        User u2 = User.builder().id(null).name("Luiz").email("luiz@email.com").password("123").build();

        repository.saveAll(List.of(u1, u2));

    }
}
