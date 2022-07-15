package com.caroline.user.api.service;


import com.caroline.user.api.exception.DataIntegrityViolationException;
import com.caroline.user.api.exception.NotFoundException;
import com.caroline.user.api.model.DTO.UserDTO;
import com.caroline.user.api.model.entity.User;
import com.caroline.user.api.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceImplTest {

    public static final Integer ID = 1;
    public static final String NAME = "Valdir";
    public static final String EMAIL = "valdir@email.com";
    public static final String PASSWORD = "123";
    public static final String MESSAGE_USER_NOT_FOUND = "User not found";
    public static final String MESSAGE_EXISTING_EMAIL = "Email already registered";

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository repository;

    @Mock
    private ModelMapper mapper;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenFindByIdShouldReturnAnUser(){

        Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.of(createNewUser()));

        User response = service.findById(ID);

        assertNotNull(response);
        assertEquals(User.class, response.getClass());
        assertEquals(createNewUser(), response);
    }

    @Test
    void whenFindByIdShouldThrowNotFoundException(){

        Mockito.when(repository.findById(Mockito.anyInt())).thenThrow(new NotFoundException(MESSAGE_USER_NOT_FOUND));

        Throwable ex = Assertions.catchThrowable(() -> service.findById(Mockito.anyInt()));

        assertEquals(NotFoundException.class, ex.getClass());
        assertEquals(MESSAGE_USER_NOT_FOUND, ex.getMessage());

    }

    @Test
    void whenFindAllShouldReturnListOfUsers(){

        Mockito.when(repository.findAll()).thenReturn(List.of(createNewUser()));

        List<User> response = service.findAll();

        //aqui o instrutor colocou notnull, mas lista não retorna nula e sim vazia certo?
        // O teste não está fazendo nenhuma validação de fato??
        assertEquals(1, response.size());
        assertEquals(User.class, response.get(0).getClass());
        assertEquals(createNewUser(), response.get(0));
        //aqui eu comparei só o obj inteiro, o instrutor cada atributo,
        //se o equalsandhashcode ta comparando todos obj, assim não tem problema?
    }

    @Test
    void whenCreateShouldReturnAnUser(){

        Mockito.when(repository.existsByEmailAndIdNot(Mockito.anyString(), Mockito.anyInt())).thenReturn(false);
        Mockito.when(repository.save(Mockito.any())).thenReturn(createNewUser());

        User response = service.create(createNewUserDTO());

        assertNotNull(response);
        assertEquals(User.class, response.getClass());
        assertEquals(createNewUser(), response);

    }

    
    @Test
    void whenCreateShouldThrowDataIntegrityViolationException(){

        Mockito.when(repository.existsByEmailAndIdNot(Mockito.anyString(), Mockito.anyInt())).thenReturn(true);

        Throwable ex = Assertions.catchThrowable(() -> service.create(createNewUserDTO()));

        assertEquals(DataIntegrityViolationException.class, ex.getClass());
        assertEquals(MESSAGE_EXISTING_EMAIL, ex.getMessage());

    }

    @Test
    void whenUpdateShouldReturnAnUser(){
        //está atualizando de fato?
        // o repository não deveria receber um obj diferente com algum dado diferente no lugar de any?

        User userUpdated = createNewUser();
        userUpdated.setPassword("987");

        UserDTO userDTOUpdated = createNewUserDTO();
        userDTOUpdated.setPassword("987");

        Mockito.when(repository.existsByEmailAndIdNot(Mockito.anyString(), Mockito.anyInt())).thenReturn(false);
        Mockito.when(repository.save(Mockito.any())).thenReturn(userUpdated);

        User response = service.update(userDTOUpdated);

        assertNotNull(response);
        assertEquals(User.class, response.getClass());
        //aqui não deveria comparar o obj passado no mock do repository? o instrutor compara as constantes
        //Porém quando colocar um obj no lugar de any, o response volta nulo
        assertEquals(userUpdated.getId(), response.getId());
        assertEquals(userUpdated.getName(), response.getName());
        assertEquals(userUpdated.getEmail(), response.getEmail());
        assertEquals(userDTOUpdated.getPassword(), response.getPassword());

    }


    @Test
    void whenUpdateShouldThrowDataIntegrityViolationException(){

        Mockito.when(repository.existsByEmailAndIdNot(Mockito.anyString(), Mockito.anyInt())).thenReturn(true);

        Throwable ex = Assertions.catchThrowable(() -> service.create(createNewUserDTO()));

        assertEquals(DataIntegrityViolationException.class, ex.getClass());
        assertEquals(MESSAGE_EXISTING_EMAIL, ex.getMessage());
    }

    @Test
    void whenDeleteWithSuccess(){

        Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.of(createNewUser()));
        Mockito.doNothing().when(repository).deleteById(Mockito.anyInt());

        service.delete(ID);

        Mockito.verify(repository, Mockito.times(1)).deleteById(Mockito.anyInt());
    }

    @Test
    void whenDeleteNotFoundException(){

        Mockito.when(repository.findById(Mockito.anyInt())).thenThrow(new NotFoundException(MESSAGE_USER_NOT_FOUND));

        Throwable ex = Assertions.catchThrowable(() -> service.findById(Mockito.anyInt()));

        assertEquals(NotFoundException.class, ex.getClass());
        assertEquals(MESSAGE_USER_NOT_FOUND, ex.getMessage());
    }

    private User createNewUser(){
        return User.builder()
                .id(ID)
                .name(NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }

    private UserDTO createNewUserDTO() {
        return UserDTO.builder()
                .id(ID)
                .name(NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }
}
