package com.caroline.user.api.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ResourceExceptionHandlerTest {

    @InjectMocks
    private ResourceExceptionHandler exceptionHandler;

    public static final String MESSAGE_USER_NOT_FOUND = "User not found";

    public static final String MESSAGE_EXISTING_EMAIL = "Email already registered";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenNotFoundException() {

        ResponseEntity<StandardError> response = exceptionHandler
                .notFound(new NotFoundException(MESSAGE_USER_NOT_FOUND), new MockHttpServletRequest());

        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(StandardError.class, response.getBody().getClass());
        assertEquals(MESSAGE_USER_NOT_FOUND, response.getBody().getError());
        assertEquals(404, response.getBody().getStatus());
        assertNotEquals("2022-07-15T12:58:04.333238", response.getBody().getTimestamp());
        assertNotEquals("/user/1", response.getBody().getPath());
    }

    @Test
    void whenDataIntegrityViolation() {

        ResponseEntity<StandardError> response = exceptionHandler
                .dataIntegrityViolation(new DataIntegrityViolationException(MESSAGE_EXISTING_EMAIL),
                        new MockHttpServletRequest());

        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(StandardError.class, response.getBody().getClass());
        assertEquals(MESSAGE_EXISTING_EMAIL, response.getBody().getError());
        assertEquals(400, response.getBody().getStatus());
        assertNotEquals("2022-07-15T12:58:04.333238", response.getBody().getTimestamp());
        assertNotEquals("/user/1", response.getBody().getPath());
    }
}