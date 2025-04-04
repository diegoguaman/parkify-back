package com.igrowker.feature.parkify.controllers;

import com.igrowker.feature.parkify.dto.LoginRequest;
import com.igrowker.feature.parkify.dto.LoginResponse;
import com.igrowker.feature.parkify.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private final String testToken = "mockJwtToken123";

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");
    }

    @Test
    void login_Success_ShouldReturnOkWithToken() {
        when(authService.login(any(LoginRequest.class))).thenReturn(testToken);

        final ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(testToken, response.getBody().getToken())
        );
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void login_ServiceThrowsException_ShouldPropagateException() {
        final RuntimeException expectedException = new RuntimeException("Authentication failed");
        when(authService.login(any(LoginRequest.class))).thenThrow(expectedException);

        final RuntimeException thrownException = assertThrows(
                RuntimeException.class, () -> authController.login(loginRequest)
        );

        assertEquals(expectedException, thrownException);
        verify(authService, times(1)).login(loginRequest);
    }
}