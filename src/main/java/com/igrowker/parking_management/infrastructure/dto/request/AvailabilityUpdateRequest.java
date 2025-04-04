package com.igrowker.parking_management.infrastructure.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the request to update parking spot availability.
 * Used in the body of PATCH /parkings/{parkingId}/availability.
 * // --- Comentario en Español ---
 * // DTO para la solicitud de actualización de disponibilidad.
 * // Se usa en el cuerpo (body) de la petición PATCH /parkings/{parkingId}/availability.
 */
@Data // Lombok: genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: genera constructor sin argumentos
@AllArgsConstructor // Lombok: genera constructor con todos los argumentos
public class AvailabilityUpdateRequest {

    /**
     * The new number of available parking spots.
     * Must be non-negative.
     * Example: 15
     * // --- Comentario en Español ---
     * // El nuevo número de plazas disponibles.
     * // Debe ser no negativo. Ejemplo: 15
     */
    @NotNull(message = "Available spots cannot be null.") // El campo es obligatorio
    @Min(value = 0, message = "Available spots cannot be negative.") // El valor no puede ser negativo
    private Integer availableSpots;
}
