package com.igrowker.feature.parkify.features.parkingV2.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.ShiftRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.ShiftResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.service.ShiftService;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v2/parkings/{parkingId}/shifts")
@Tag(name = "Shifts", description = "Shift management by parking")
@SecurityRequirement(name = "bearerAuth")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @Operation(summary = "Create a new shift")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Shift successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid or missing data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ShiftResponseDTO> create(
        @Parameter(description = "Parking ID", required = true)
        @PathVariable UUID parkingId,
        @Parameter(description = "New shift data", required = true)
        @Valid @RequestBody ShiftRequestDTO dto
    ) {
        ShiftResponseDTO response = shiftService.create(parkingId, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all shifts for the parking")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Shifts retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Parking not found")
    })
    @GetMapping
    public List<ShiftResponseDTO> getByParking(
            @Parameter(description = "Parking ID", required = true)
            @PathVariable UUID parkingId
    ) {
        return shiftService.findByParkingId(parkingId);
    }

    @Operation(summary = "Update an existing shift")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Shift successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "404", description = "Shift not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{shiftId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ShiftResponseDTO> update(
            @Parameter(description = "Parking ID", required = true)
            @PathVariable UUID parkingId,
            @Parameter(description = "ID of the shift to update", required = true)
            @PathVariable UUID shiftId,
            @Parameter(description = "New shift data", required = true)
            @Valid @RequestBody ShiftRequestDTO dto
    ) {
        ShiftResponseDTO updated = shiftService.update(parkingId, shiftId, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a shift")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Shift successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Shift not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{shiftId}")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Parking ID", required = true)
            @PathVariable UUID parkingId,
            @Parameter(description = "ID of the shift to delete", required = true)
            @PathVariable UUID shiftId
    ) {
        shiftService.delete(parkingId, shiftId);
    }
}