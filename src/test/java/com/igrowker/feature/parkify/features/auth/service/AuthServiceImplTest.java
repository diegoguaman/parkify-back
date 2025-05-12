package com.igrowker.feature.parkify.features.auth.service;

import com.igrowker.feature.parkify.exception.EmailAlreadyExistsException;
import com.igrowker.feature.parkify.features.auth.dto.request.RegisterRequest;
import com.igrowker.feature.parkify.features.auth.dto.response.RegisterResponse;
import com.igrowker.feature.parkify.features.auth.entities.AuthUser;
import com.igrowker.feature.parkify.features.auth.entities.Role;
import com.igrowker.feature.parkify.features.auth.repository.AuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthServiceImpl authService;

    @Captor
    private ArgumentCaptor<AuthUser> authUserArgumentCaptor;

    private RegisterRequest registerRequest;
    private AuthUser savedAuthUserMock;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "NewUser",
                "newuser@example.com",
                "password123",
                "OWNER",
                "0123456789"
        );
        savedAuthUserMock = AuthUser.builder()
                .id(1L)
                .email(registerRequest.email())
                .password("encodedPassword")
                .role(Role.valueOf(registerRequest.role()))
                .username(registerRequest.username())
                .contactPhone(registerRequest.contactPhone())
                .createdAt(LocalDateTime.now().minusSeconds(1))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void register_Success_ShouldReturnCorrectRegisterResponseData() {
        when(authUserRepository.findByEmail(registerRequest.email()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.password()))
                .thenReturn("encodedPassword");
        when(authUserRepository.save(any(AuthUser.class)))
                .thenReturn(savedAuthUserMock);

        final RegisterResponse response = authService.register(registerRequest);

        assertNotNull(response, "Response should not be null");
        verify(authUserRepository, times(1)).save(authUserArgumentCaptor.capture());
        final AuthUser capturedUser = authUserArgumentCaptor.getValue();
        assertAll("Captured AuthUser to be saved",
                () -> assertEquals(registerRequest.email(), capturedUser.getEmail()),
                () -> assertEquals("encodedPassword", capturedUser.getPassword()),
                () -> assertEquals(Role.valueOf(registerRequest.role()), capturedUser.getRole()),
                () -> assertEquals(registerRequest.username(), capturedUser.getUsername()),
                () -> assertEquals(registerRequest.contactPhone(), capturedUser.getContactPhone())
        );
        assertAll("RegisterResponse content",
                () -> assertEquals(String.valueOf(savedAuthUserMock.getId()), response.getId()),
                () -> assertEquals(savedAuthUserMock.getUsername(), response.getUsername()),
                () -> assertEquals(savedAuthUserMock.getEmail(), response.getEmail()),
                () -> assertEquals(savedAuthUserMock.getRole().name(), response.getRole()),
                () -> assertEquals(savedAuthUserMock.getContactPhone(), response.getContactPhone()),
                () -> assertEquals(savedAuthUserMock.getCreatedAt(), response.getCreatedAt()),
                () -> assertEquals(savedAuthUserMock.getUpdatedAt(), response.getUpdatedAt())
        );
    }

    @Test
    void register_EmailAlreadyExists_ShouldThrowEmailAlreadyExistsException() {
        when(authUserRepository.findByEmail(registerRequest.email()))
                .thenReturn(Optional.of(new AuthUser()));

        final EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () ->
                authService.register(registerRequest)
        );

        assertEquals("This email already registered.", exception.getMessage());
        verify(authUserRepository, never()).save(any());
    }

    @Test
    void register_NullRoleInRequest_ShouldUseDefaultRole() {
        final RegisterRequest requestWithNullRole = new RegisterRequest(
                "TestUserNullRole",
                "nullrole@example.com",
                "password123",
                null,
                "1234567890"
        );
        final AuthUser savedUserWithDefaultRole = AuthUser.builder()
                .id(2L)
                .email(requestWithNullRole.email())
                .password("encodedPasswordForNullRole")
                .role(Role.OWNER)
                .username(requestWithNullRole.username())
                .contactPhone(requestWithNullRole.contactPhone())
                .createdAt(LocalDateTime.now().minusSeconds(1))
                .updatedAt(LocalDateTime.now())
                .build();

        when(authUserRepository.findByEmail(requestWithNullRole.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(requestWithNullRole.password())).thenReturn("encodedPasswordForNullRole");
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(savedUserWithDefaultRole);

        authService.register(requestWithNullRole);

        verify(authUserRepository).save(authUserArgumentCaptor.capture());
        final AuthUser capturedUser = authUserArgumentCaptor.getValue();
        assertEquals(Role.OWNER, capturedUser.getRole(), "Default role should be OWNER when request role is null");
    }

    @Test
    void register_NullUsernameInRequest_ShouldUseEmptyStringForUsername() {
        final RegisterRequest requestWithNullUsername = new RegisterRequest(
                null,
                "nullusername@example.com",
                "password123",
                "DRIVER",
                "1234567890"
        );
        final AuthUser savedUserWithEmptyUsername = AuthUser.builder()
                .id(3L)
                .email(requestWithNullUsername.email())
                .password("encodedPasswordForNullUser")
                .role(Role.DRIVER)
                .username("")
                .contactPhone(requestWithNullUsername.contactPhone())
                .createdAt(LocalDateTime.now().minusSeconds(1))
                .updatedAt(LocalDateTime.now())
                .build();

        when(authUserRepository.findByEmail(requestWithNullUsername.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(requestWithNullUsername.password())).thenReturn("encodedPasswordForNullUser");
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(savedUserWithEmptyUsername);

        authService.register(requestWithNullUsername);

        verify(authUserRepository).save(authUserArgumentCaptor.capture());
        final AuthUser capturedUser = authUserArgumentCaptor.getValue();
        assertEquals("", capturedUser.getUsername(), "Username should be empty string when request username is null");
    }
}