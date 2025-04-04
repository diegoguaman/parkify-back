package com.igrowker.common.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter // Lombok
@Setter // Lombok (para que el servicio pueda actualizar)
@NoArgsConstructor // JPA requiere un constructor sin argumentos
@Entity
@Table(name="parking")
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ... otros campos (name, address, location, totalSpots, tariffs, workingHours, whatsappNumber)...

    /**
     * Identifier of the user (owner) who owns this parking lot.
     * REQUIRED for permission checks in ParkingManagementService.
     * // --- Comentario en Español ---
     * // Identificador del usuario (propietario) dueño de este parking.
     * // NECESARIO para verificar permisos en ParkingManagementService.
     */
    @Column(nullable = false) // Ejemplo de columna en BD
    private String ownerId;

    /**
     * The current number of spots available for booking.
     * Updated via PATCH /parkings/{parkingId}/availability.
     * REQUIRED for this feature.
     * // --- Comentario en Español ---
     * // Número actual de plazas disponibles para reservar.
     * // Se actualiza mediante PATCH /parkings/{parkingId}/availability.
     * // NECESARIO para esta funcionalidad.
     */
    @Column // Ejemplo de columna en BD
    private Integer currentAvailableSpots;

    // --- Constructores, otros métodos ---

    /**
     * Example domain logic method (could be in service or here).
     * Validates that the new availability does not exceed total spots.
     * // --- Comentario en Español ---
     * // Ejemplo de un método con lógica de dominio (podría estar en el servicio o aquí).
     * // Valida que la nueva disponibilidad no exceda el total de plazas.
     * @param newAvailableSpots The new value.
     * @throws IllegalArgumentException if the value is invalid.
     */
    public void updateAvailableSpots(Integer newAvailableSpots) {
        if (newAvailableSpots == null || newAvailableSpots < 0) {
            throw new IllegalArgumentException("Available spots cannot be null or negative.");
            // --- Comentario en Español ---
            // Lanzar excepción si el valor es nulo o negativo.
        }
        // Optional check: not more than total spots
        // --- Comentario en Español ---
        // Verificación opcional: que no sea mayor al total de plazas.
        // if (this.totalSpots != null && newAvailableSpots > this.totalSpots) {
        //     throw new IllegalArgumentException("Available spots cannot exceed total spots (" + this.totalSpots + ").");
        // }
        this.setCurrentAvailableSpots(newAvailableSpots);
    }
}
