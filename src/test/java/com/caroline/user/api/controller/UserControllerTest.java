package com.caroline.user.api.controller;

import com.caroline.user.api.exception.DataIntegrityViolationException;
import com.caroline.user.api.exception.NotFoundException;
import com.caroline.user.api.model.DTO.UserDTO;
import com.caroline.user.api.model.entity.User;
import com.caroline.user.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    public static final Integer ID = 1;
    public static final String NAME = "Valdir";
    public static final String EMAIL = "valdir@email.com";
    public static final String PASSWORD = "123";

    public static final String MESSAGE_USER_NOT_FOUND = "User not found";

    public static final String MESSAGE_EXISTING_EMAIL = "Email already registered";

    public static final String USER_API = "/user";

    @InjectMocks
    private UserController controller;

    @Autowired
    MockMvc mockMvc;

    @Mock
    private ModelMapper mapper;

    @Mock
    private UserService service;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    //se é um teste de requisições, não deveriámos testas as requisições chamando endpoint?

    @Test
    void whenFindByIdShouldReturnAnUserDTO(){

        Mockito.when(service.findById(Mockito.anyInt())).thenReturn(createNewUser());
        Mockito.when(mapper.map(Mockito.any(), Mockito.any())).thenReturn(createNewUserDTO());

        ResponseEntity<UserDTO> response = controller.findById(ID);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(UserDTO.class, response.getBody().getClass());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ID, response.getBody().getId());
        //aqui o instrutor comparou todos os atributos, mas se o obj não foi mockado e passsado só o id, faz sentido?
    }

    @Test
    void whenFindByIdShouldReturnHttpStatusNotFound() throws Exception {

        Mockito.when(service.findById(Mockito.anyInt()))
                .thenThrow(new NotFoundException(MESSAGE_USER_NOT_FOUND));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API + "/5")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    void whenFindByAllShouldReturnAListOfUserDTO() {

        Mockito.when(service.findAll()).thenReturn(List.of(createNewUser()));
        Mockito.when(mapper.map(Mockito.any(), Mockito.any())).thenReturn(createNewUserDTO());

        ResponseEntity<List<UserDTO>> response = controller.findAll();

        //aqui o instrutor colocou notnull, mas lista não retorna nula e sim vazia certo?
        // O teste não está fazendo nenhuma validação de fato??
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(ArrayList.class, response.getBody().getClass());
        assertEquals(UserDTO.class, response.getBody().get(0).getClass());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        //aqui o instrutor comparou todos os atributos, mas se o obj não foi mockado de fato, só o retorno, faz sentido?
    }

    @Test
    void whenFindByAllShouldReturnAListEmpty(){

        Mockito.when(service.findAll()).thenReturn(List.of());
        Mockito.when(mapper.map(Mockito.any(), Mockito.any())).thenReturn(List.of());

        ResponseEntity<List<UserDTO>> response = controller.findAll();

        assert(response.getBody().isEmpty());
        assertNotNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(ArrayList.class, response.getBody().getClass());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void whenCreateShouldReturnHttpStatusCreated(){

        Mockito.when(service.create(Mockito.any())).thenReturn(createNewUser());

        ResponseEntity<UserDTO> response = controller.create(createNewUserDTO());

        assertNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().get("Location"));

    }

    @Test
    void whenCreateShouldReturnHttpStatusBadRequest() throws Exception {

        Mockito.when(service.create(Mockito.any()))
                .thenThrow(new DataIntegrityViolationException(MESSAGE_EXISTING_EMAIL));

        String json = new ObjectMapper().writeValueAsString(createNewUserDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USER_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value(MESSAGE_EXISTING_EMAIL));
    }

    @Test
    void whenUpdateShouldReturnAnUserDTO(){

        UserDTO userDTO = createNewUserDTO();
        userDTO.setPassword("987");

        User user = createNewUser();
        user.setPassword("987");

        Mockito.when(service.update(userDTO)).thenReturn(user);
        Mockito.when(mapper.map(Mockito.any(), Mockito.any())).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = controller.update(ID, userDTO);

        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(UserDTO.class, response.getBody().getClass());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ID, response.getBody().getId());

        assertEquals(userDTO.getName(), response.getBody().getName());
        assertEquals(userDTO.getEmail(), response.getBody().getEmail());
        assertEquals(userDTO.getPassword(), response.getBody().getPassword());
    }

    @Test
    void whenUpdateShouldReturnHttpStatusBadRequest() throws Exception {

        UserDTO userDTO = createNewUserDTO();
        userDTO.setPassword("987");

        Mockito.when(service.update(Mockito.any()))
                .thenThrow(new DataIntegrityViolationException(MESSAGE_EXISTING_EMAIL));

        String json = new ObjectMapper().writeValueAsString(userDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USER_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value(MESSAGE_EXISTING_EMAIL));
    }

    @Test
    void whenDeleteShouldReturnHttpStatusNoContent(){

        Mockito.doNothing().when(service).delete(Mockito.anyInt());

        ResponseEntity<UserDTO> response = controller.delete(ID);

        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Mockito.verify(service, Mockito.times(1)).delete(Mockito.anyInt());
    }

    @Test
    void whenDeleteShouldReturnHttpStatusNotFound() throws Exception {

        Mockito.doNothing().when(service).delete(Mockito.anyInt());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API + "/5")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    private User createNewUser() {
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
