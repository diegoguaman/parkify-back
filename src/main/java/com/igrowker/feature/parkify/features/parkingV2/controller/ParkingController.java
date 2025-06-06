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
@Tag(name = "Parking V2", description = "CRUD para parkings con turnos y tarifas dinámicas")
@SecurityRequirement(name = "bearerAuth")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Operation(summary = "Obtener todos los parkings disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de parkings obtenida correctamente")
    @GetMapping
    public List<ParkingResponseDTO> getAll() {
        return parkingService.getAll();
    }

    @Operation(summary = "Obtener un parking por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Parking encontrado"),
        @ApiResponse(responseCode = "404", description = "Parking no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}")
    public ParkingResponseDTO getById(
            @Parameter(description = "ID del parking", required = true) @PathVariable UUID id
    ) {
        return parkingService.getById(id);
    }

    @Operation(summary = "Crear un nuevo parking")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Parking creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o faltantes"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ParkingResponseDTO create(
            @Parameter(description = "Datos del nuevo parking", required = true)
            @Valid @RequestBody ParkingRequestDTO dto
    ) {
        return parkingService.create(dto);
    }

    @Operation(summary = "Actualizar un parking existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Parking actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Parking no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ParkingResponseDTO update(
            @Parameter(description = "ID del parking a actualizar", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Nuevos datos del parking", required = true)
            @Valid @RequestBody ParkingRequestDTO dto
    ) {
        return parkingService.update(id, dto);
    }

    @Operation(summary = "Eliminar un parking")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Parking eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Parking no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public void delete(
            @Parameter(description = "ID del parking a eliminar", required = true)
            @PathVariable UUID id
    ) {
        parkingService.delete(id);
    }
}
