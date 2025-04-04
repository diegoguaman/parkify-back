package com.igrowker.parking_management.application.impl;

import com.igrowker.common.exceptions.BadRequestException;
import com.igrowker.common.exceptions.ResourceNotFoundException;
import com.igrowker.parking_management.application.ParkingManagementService;
import com.igrowker.parking_management.infrastructure.dto.request.AvailabilityUpdateRequest;
import com.igrowker.parking_management.infrastructure.dto.response.AvailabilityUpdateResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ParkingManagementServiceImpl implements ParkingManagementService {
    @Override
    public AvailabilityUpdateResponse updateAvailability(String parkingId, AvailabilityUpdateRequest request, String ownerId) throws ResourceNotFoundException, AccessDeniedException, BadRequestException {
        return null;
    }
}
