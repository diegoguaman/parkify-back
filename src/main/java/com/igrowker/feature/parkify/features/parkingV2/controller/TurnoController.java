package com.igrowker.feature.parkify.features.parkingV2.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.TurnoRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.TurnoResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.service.TurnoService;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v2/turnos")
@Tag(name = "Turnos", description = "Gestión de turnos por parking")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @PostMapping
    public TurnoResponseDTO create(@RequestBody TurnoRequestDTO dto) {
        return turnoService.create(dto);
    }

    @GetMapping("/parking/{parkingId}")
    public List<TurnoResponseDTO> getByParking(@PathVariable UUID parkingId) {
        return turnoService.findByParkingId(parkingId);
    }

    @PutMapping("/{turnoId}")
    public ResponseEntity<TurnoResponseDTO> update(
            @PathVariable UUID turnoId,
            @RequestBody TurnoRequestDTO dto) {
        TurnoResponseDTO updated = turnoService.update(turnoId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{turnoId}")
    public void delete(@PathVariable UUID turnoId) {
        turnoService.delete(turnoId);
    }
}
