package com.igrowker.feature.parkify.controllers;

import com.igrowker.feature.parkify.dto.LoginRequest;
import com.igrowker.feature.parkify.dto.LoginResponse;
import com.igrowker.feature.parkify.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
// @CrossOrigin(origins = "*") // TODO: Configurar CORS correctamente despues
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        final String token = authService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
