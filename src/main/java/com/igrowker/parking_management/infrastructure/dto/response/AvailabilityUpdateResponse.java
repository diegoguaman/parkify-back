package com.igrowker.parking_management.infrastructure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for the response to a successful availability update.
 * Returned on successful execution of PATCH /parkings/{parkingId}/availability.
 * // --- Comentario en Español ---
 * // DTO para la respuesta cuando la actualización de disponibilidad es exitosa.
 * // Se devuelve al completar con éxito la petición PATCH /parkings/{parkingId}/availability.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityUpdateResponse {

    /**
     * ID of the updated parking lot.
     * Example: "p123xyz"
     * // --- Comentario en Español ---
     * // ID del parking que fue actualizado. Ejemplo: "p123xyz"
     */
    private String parkingId;

    /**
     * The updated (current) number of available spots after the update.
     * Example: 15
     * // --- Comentario en Español ---
     * // El número actualizado de plazas disponibles después de la operación. Ejemplo: 15
     */
    private int availableSpots; // Usamos int primitivo, ya no se espera null aquí
}
