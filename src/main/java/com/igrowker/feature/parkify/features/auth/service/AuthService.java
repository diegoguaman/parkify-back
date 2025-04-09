package com.igrowker.feature.parkify.features.auth.service;


import com.igrowker.feature.parkify.features.auth.dto.request.LoginRequest;
import com.igrowker.feature.parkify.features.auth.dto.response.RegisterResponse;
import com.igrowker.feature.parkify.features.user.dto.RegisterRequest;
import jakarta.validation.Valid;

public interface AuthService {
    String login(LoginRequest request);
    RegisterResponse register(@Valid RegisterRequest request);
}
