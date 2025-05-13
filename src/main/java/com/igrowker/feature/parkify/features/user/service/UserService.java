package com.igrowker.feature.parkify.features.user.service;

import com.igrowker.feature.parkify.features.user.dto.response.PublicUserResponse;

public interface UserService {

    PublicUserResponse getPublicUserById(String userId);
}
