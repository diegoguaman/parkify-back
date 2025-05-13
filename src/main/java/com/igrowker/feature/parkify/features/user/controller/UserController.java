package com.igrowker.feature.parkify.features.user.controller;

import com.igrowker.feature.parkify.features.user.dto.response.PublicUserResponse;
import com.igrowker.feature.parkify.features.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "User-related operations")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // part of #16 and #23
    @GetMapping("/{userId}")
    public ResponseEntity<PublicUserResponse> getUserById(@PathVariable String userId) {
        PublicUserResponse userResponse = userService.getPublicUserById(userId);
        return ResponseEntity.ok(userResponse);
    }
}
