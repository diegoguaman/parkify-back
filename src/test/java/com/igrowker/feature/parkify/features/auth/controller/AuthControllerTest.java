package com.igrowker.feature.parkify.features.auth.controller;

import com.igrowker.feature.parkify.common.service.UriBuilderService;
import com.igrowker.feature.parkify.features.auth.dto.request.LoginRequest;
import com.igrowker.feature.parkify.features.auth.dto.request.RegisterRequest;
import com.igrowker.feature.parkify.features.auth.dto.response.LoginResponse;
import com.igrowker.feature.parkify.features.auth.dto.response.RegisterResponse;
import com.igrowker.feature.parkify.features.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.LocalDateTime;

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
    @Mock
    private UriBuilderService uriBuilderService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private final String testEmail = "test@example.com";
    private RegisterRequest registerRequest;
    private final String expectedUserId = "12345";
    private final String expectedUsername = "kris";
    private final String expectedEmailForRegister = "kris@example.com";
    private final String expectedRole = "OWNER";
    private final String expectedPhone = "0123456789";
    private final LocalDateTime mockCreatedAt = LocalDateTime.now().minusDays(1);
    private final LocalDateTime mockUpdatedAt = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest(testEmail, "password");
        registerRequest = new RegisterRequest(
                expectedUsername,
                expectedEmailForRegister,
                "Password",
                expectedRole,
                expectedPhone
        );
    }

    @Test
    void login_Success_ShouldReturnOkWithToken() {
        String localTestToken = "mockJwtToken123";
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse(localTestToken, testEmail));

        final ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(localTestToken, response.getBody().token()),
                () -> assertEquals(testEmail, response.getBody().email())
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

    @Test
    void register_Success_ShouldReturnCreatedWithUserData() {
        final URI mockLocation = URI.create("http://mock-location/api/v1/users/" + expectedUserId);
        when(uriBuilderService.buildUserLocationUri(expectedUserId)).thenReturn(mockLocation);

        final RegisterResponse mockServiceResponse = RegisterResponse.builder()
                .id(expectedUserId)
                .username(expectedUsername)
                .email(expectedEmailForRegister)
                .role(expectedRole)
                .contactPhone(expectedPhone)
                .createdAt(mockCreatedAt)
                .updatedAt(mockUpdatedAt)
                .build();
        when(authService.register(any(RegisterRequest.class))).thenReturn(mockServiceResponse);

        final ResponseEntity<RegisterResponse> responseEntity = authController.register(registerRequest);

        assertAll(
                () -> assertNotNull(responseEntity),
                () -> assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode()),
                () -> assertNotNull(responseEntity.getBody()),
                () -> assertEquals(expectedUserId, responseEntity.getBody().getId()),
                () -> assertEquals(expectedUsername, responseEntity.getBody().getUsername()),
                () -> assertEquals(expectedEmailForRegister, responseEntity.getBody().getEmail()),
                () -> assertEquals(expectedRole, responseEntity.getBody().getRole()),
                () -> assertEquals(expectedPhone, responseEntity.getBody().getContactPhone()),
                () -> assertEquals(mockCreatedAt, responseEntity.getBody().getCreatedAt()),
                () -> assertEquals(mockUpdatedAt, responseEntity.getBody().getUpdatedAt()),
                () -> assertEquals(mockLocation, responseEntity.getHeaders().getLocation())
        );

        verify(authService, times(1)).register(registerRequest);
        verify(uriBuilderService, times(1))
                .buildUserLocationUri(expectedUserId);
    }

    @Test
    void register_ServiceThrowsException_ShouldPropagateException() {
        final RuntimeException expectedException = new RuntimeException("Register failed");
        when(authService.register(any(RegisterRequest.class))).thenThrow(expectedException);

        final RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> authController.register(registerRequest)
        );

        assertEquals(expectedException, thrownException);
        verify(authService, times(1)).register(registerRequest);
    }
}