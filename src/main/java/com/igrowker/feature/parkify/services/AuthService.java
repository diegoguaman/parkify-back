package com.igrowker.feature.parkify.services;

import com.igrowker.feature.parkify.dto.LoginRequest;

public interface AuthService {
    String login(LoginRequest request);
}
