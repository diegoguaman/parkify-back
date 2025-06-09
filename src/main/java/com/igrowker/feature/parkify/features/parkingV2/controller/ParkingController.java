package com.igrowker.feature.parkify.features.parkingV2.controller;
import com.igrowker.feature.parkify.features.parkingV2.dto.request.ParkingRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.ParkingResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.service.ParkingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("parkingV2Controller")
@RequestMapping("/api/v2/parkings")
@Tag(name = "Parking V2", description = "CRUD for parkings with shifts and dynamic pricing")
@SecurityRequirement(name = "bearerAuth")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Operation(summary = "Get all available parkings")
    @ApiResponse(responseCode = "200", description = "List of parkings retrieved successfully")
    @GetMapping
    public List<ParkingResponseDTO> getAll() {
        return parkingService.getAll();
    }

    @Operation(summary = "Get a parking by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Parking found"),
        @ApiResponse(responseCode = "404", description = "Parking not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    public ParkingResponseDTO getById(
            @Parameter(description = "Parking ID", required = true) @PathVariable UUID id
    ) {
        return parkingService.getById(id);
    }

    @Operation(summary = "Create a new parking")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Parking successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid or missing data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ParkingResponseDTO create(
            @Parameter(description = "New parking data", required = true)
            @Valid @RequestBody ParkingRequestDTO dto
    ) {
        return parkingService.create(dto);
    }

    @Operation(summary = "Update an existing parking")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Parking successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "404", description = "Parking not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ParkingResponseDTO update(
            @Parameter(description = "ID of the parking to update", required = true)
            @PathVariable UUID id,
            @Parameter(description = "New parking data", required = true)
            @Valid @RequestBody ParkingRequestDTO dto
    ) {
        return parkingService.update(id, dto);
    }

    @Operation(summary = "Delete a parking")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Parking successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Parking not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public void delete(
            @Parameter(description = "ID of the parking to delete", required = true)
            @PathVariable UUID id
    ) {
        parkingService.delete(id);
    }
}
