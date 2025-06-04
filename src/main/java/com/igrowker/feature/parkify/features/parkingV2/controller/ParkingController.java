package com.igrowker.feature.parkify.features.parkingV2.controller;
import com.igrowker.feature.parkify.features.parkingV2.dto.request.ParkingRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.ParkingResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.service.ParkingService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/parkings")
@Tag(name = "Parking V2", description = "CRUD para parkings con turnos y tarifas dinámicas")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping
    public List<ParkingResponseDTO> getAll() {
        return parkingService.getAll();
    }

    @GetMapping("/{id}")
    public ParkingResponseDTO getById(@PathVariable UUID id) {
        return parkingService.getById(id);
    }

    @PostMapping
    public ParkingResponseDTO create(@RequestBody ParkingRequestDTO dto) {
        return parkingService.create(dto);
    }

    @PutMapping("/{id}")
    public ParkingResponseDTO update(@PathVariable UUID id, @RequestBody ParkingRequestDTO dto) {
        return parkingService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        parkingService.delete(id);
    }
}