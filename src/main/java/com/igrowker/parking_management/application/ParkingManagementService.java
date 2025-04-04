package com.igrowker.parking_management.application;

import com.igrowker.parking_management.infrastructure.dto.request.AvailabilityUpdateRequest;
import com.igrowker.parking_management.infrastructure.dto.response.AvailabilityUpdateResponse;
// --- DTOs y otros tipos para otros métodos de gestión ---
// import com.igrowker.parking_management.infrastructure.dto.request.CreateParkingRequest;
// import com.igrowker.parking_management.infrastructure.dto.request.UpdateParkingRequest;
// import com.igrowker.parking_management.infrastructure.dto.response.ParkingDto;
// import com.igrowker.parking_management.infrastructure.dto.response.ParkingSummaryDto;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;

// --- Excepciones personalizadas (referencias) ---
import com.igrowker.parking_management.infrastructure.dto.response.AvailabilityUpdateResponse;
import com.igrowker.common.exceptions.ResourceNotFoundException;
import com.igrowker.common.exceptions.BadRequestException;
import org.springframework.security.access.AccessDeniedException;

/**
 * Service layer interface for parking lot management.
 * Defines business operations available to parking owners.
 * // --- Comentario en Español ---
 * // Interfaz de la capa de servicio para la gestión de parkings.
 * // Define las operaciones de negocio disponibles para los propietarios de parkings.
 */
public interface ParkingManagementService {

    /**
     * Updates the number of available parking spots for the specified parking lot.
     *
     * Performs the following steps:
     * // --- Comentario en Español ---
     * // Lógica de negocio que debe implementar la clase de servicio:
     * 1. Find the parking lot by `parkingId`. Throws {@link ResourceNotFoundException} if not found.
     *    // 1. Buscar el parking por `parkingId`. Si no se encuentra, lanzar ResourceNotFoundException.
     * 2. Verify that the user with `ownerId` is the owner of the found parking lot.
     *    Throws {@link org.springframework.security.access.AccessDeniedException} if not. (THIS CHECK CAN BE STUBBED FOR THE EXAMPLE).
     *    // 2. Verificar que el usuario (`ownerId`) es el propietario del parking encontrado.
     *    //    Si no lo es, lanzar ForbiddenAccessException. (ESTA VERIFICACIÓN PUEDE SER SIMULADA EN EL EJEMPLO).
     * 3. Validate the `availableSpots` value from the request (e.g., not greater than totalSpots, although basic @Min(0) validation is in DTO).
     *    // 3. Validar el valor `availableSpots` según reglas de negocio (ej: no mayor que totalSpots).
     * 4. Update the `currentAvailableSpots` field in the parking entity.
     *    // 4. Actualizar el campo `currentAvailableSpots` en la entidad del parking.
     * 5. Save the updated parking entity using the repository.
     *    // 5. Guardar la entidad actualizada usando el repositorio.
     * 6. Return a DTO confirming the update.
     *    // 6. Devolver un DTO con la confirmación de la actualización.
     *
     * @param parkingId ID of the parking lot to update.
     * @param request DTO with the new number of available spots.
     * @param ownerId ID of the user (owner) performing the operation (obtained from Security Context).
     *               // --- Comentario en Español ---
     *               // ID del usuario que realiza la operación (se obtiene del contexto de seguridad).
     * @return DTO with the parking ID and the updated number of spots {@link AvailabilityUpdateResponse}.
     * @throws ResourceNotFoundException if the parking lot is not found.
     * @throws AccessDeniedException if the user is not the owner of the parking lot.
     * @throws BadRequestException if the new value is invalid according to business rules (e.g., > totalSpots).
     */
    AvailabilityUpdateResponse updateAvailability(String parkingId, AvailabilityUpdateRequest request, String ownerId)
            throws ResourceNotFoundException, AccessDeniedException, BadRequestException; // Excepciones de negocio esperadas

    // --- Other management methods (contracts) ---
    // ParkingDto createParking(CreateParkingRequest request, String ownerId);
    // Page<ParkingSummaryDto> getOwnedParkings(String ownerId, Pageable pageable);
    // ParkingDto updateParking(String parkingId, UpdateParkingRequest request, String ownerId)
    //         throws ResourceNotFoundException, ForbiddenAccessException;

}
