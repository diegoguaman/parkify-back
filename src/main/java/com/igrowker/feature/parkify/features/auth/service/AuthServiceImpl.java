package com.igrowker.feature.parkify.features.auth.service;

import com.igrowker.feature.parkify.exception.EmailAlreadyExistsException;
import com.igrowker.feature.parkify.features.auth.dto.request.LoginRequest;
import com.igrowker.feature.parkify.features.auth.dto.request.RegisterRequest;
import com.igrowker.feature.parkify.features.auth.dto.response.LoginResponse;
import com.igrowker.feature.parkify.features.auth.dto.response.RegisterResponse;
import com.igrowker.feature.parkify.features.auth.dto.response.UserResponse;
import com.igrowker.feature.parkify.features.auth.entities.AuthUser;
import com.igrowker.feature.parkify.features.auth.entities.Role;
import com.igrowker.feature.parkify.features.auth.repository.AuthUserRepository;
import com.igrowker.feature.parkify.features.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        final UserDetails userDetails = (UserDetails) authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                )
                .getPrincipal();
        final String email = userDetails.getUsername();
        final String token = jwtService.generateToken(userDetails);
        return new LoginResponse(token, email);
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (authUserRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("This email already registered.");
        }

        final AuthUser newUser = AuthUser.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role() != null ? Role.valueOf(request.role()) : Role.OWNER)
                .username(request.username() != null ? request.username() : "")
                .contactPhone(request.contactPhone() != null ? request.contactPhone() : "")
                .build();
        final AuthUser savedUser = authUserRepository.save(newUser);

        return RegisterResponse.builder()
                .id(String.valueOf(savedUser.getId()))
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .contactPhone(savedUser.getContactPhone())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserDetails(String email) {
        final AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Authenticated user not found with email: " + email)
                );
        return UserResponse.builder()
                .id(String.valueOf(authUser.getId()))
                .username(authUser.getUsername())
                .email(authUser.getEmail())
                .role(authUser.getRole().name())
                .contactPhone(authUser.getContactPhone())
                .createdAt(authUser.getCreatedAt())
                .updatedAt(authUser.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void updateEmail(String currentEmail, String newEmail) {
        AuthUser user = authUserRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (authUserRepository.findByEmail(newEmail).isPresent()) {
            throw new EmailAlreadyExistsException("El email ya está en uso");
        }

        user.setEmail(newEmail);
        authUserRepository.save(user);
    }

    //delete user
    @Override
    @Transactional
    public void deleteUser(String email) {
        log.info("Deleting user with email: {}", email);
        AuthUser user = authUserRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        authUserRepository.delete(user);
    }

}