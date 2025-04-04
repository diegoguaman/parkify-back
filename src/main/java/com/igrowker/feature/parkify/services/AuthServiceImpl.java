package com.igrowker.feature.parkify.services;

import com.igrowker.feature.parkify.dto.LoginRequest;
import com.igrowker.feature.parkify.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public String login(LoginRequest request) {
        final UserDetails userDetails = (UserDetails) authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                )
                .getPrincipal();
        return jwtUtil.generateToken(userDetails);
    }

}
