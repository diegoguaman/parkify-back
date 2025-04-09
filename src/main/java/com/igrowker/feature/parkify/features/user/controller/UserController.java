package com.igrowker.feature.parkify.features.user.controller;

import com.igrowker.feature.parkify.features.user.dto.RegisterRequest;
import com.igrowker.feature.parkify.features.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final AuthService authService;

    @Autowired
    public UserController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registra un nuevo usuario con el rol de "driver".
     * No requiere autenticación.
     *
     * @param request datos del nuevo usuario
     * @return mensaje de éxito o error
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
