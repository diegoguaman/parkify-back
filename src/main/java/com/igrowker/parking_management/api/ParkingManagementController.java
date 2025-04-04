package com.igrowker.parking_management.api;

import com.igrowker.parking_management.application.ParkingManagementService;
import com.igrowker.parking_management.infrastructure.dto.request.AvailabilityUpdateRequest;
import com.igrowker.parking_management.infrastructure.dto.response.AvailabilityUpdateResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// --- Anotaciones ---
@RestController // Define la clase como un controlador REST
@RequestMapping("/parkings") // Ruta base para todas las operaciones de parkings en este controlador
@Validated // Habilita la validación para los parámetros del método (ej: @NotBlank en @PathVariable)
public class ParkingManagementController {

    private final ParkingManagementService parkingManagementService;

    // --- Inyección de Dependencias ---
    // Usamos inyección por constructor - es la mejor práctica
    public ParkingManagementController(ParkingManagementService parkingManagementService) {
        this.parkingManagementService = parkingManagementService;
    }

    // --- Endpoint para Actualizar Disponibilidad ---

    /**
     * Updates the number of available spots for the specified parking lot.
     * HTTP Method: PATCH
     * Path: /parkings/{parkingId}/availability
     *
     * Requires authentication of a user with the 'OWNER' role, who owns this parking lot.
     * // --- Comentario en Español ---
     * // Este método maneja la solicitud PATCH para actualizar las plazas disponibles.
     * // Requiere que el usuario esté autenticado, tenga rol 'OWNER' y sea el propietario del parking.
     *
     * @param parkingId ID of the parking lot to update (from URL path). Must not be blank.
     *                 // --- Comentario en Español ---
     *                 // El ID del parking a actualizar, viene en la URL. No puede estar vacío.
     * @param request DTO with the new number of available spots (from request body). 'availableSpots' is required and cannot be negative.
     *               // --- Comentario en Español ---
     *               // El DTO que viene en el cuerpo de la solicitud. Contiene el nuevo número de plazas.
     *               // 'availableSpots' es obligatorio y debe ser >= 0 (validado por @Valid y las anotaciones en el DTO).
     * @return ResponseEntity with {@link AvailabilityUpdateResponse} DTO and status 200 OK on success.
     *         // --- Comentario en Español ---
     *         // En caso de éxito, devuelve un ResponseEntity con el DTO de respuesta y estado 200 OK.
     *
     * Possible Errors (handled by GlobalExceptionHandler):
     * // --- Comentario en Español ---
     * // Los errores son manejados centralizadamente por GlobalExceptionHandler.
     * - 400 Bad Request: If `parkingId` is blank, or `request` is invalid (e.g., `availableSpots` < 0 or null).
     *                  // Si el parkingId está vacío o el DTO 'request' no pasa la validación.
     * - 401 Unauthorized: If the user is not authenticated (handled by Spring Security).
     *                     // Si el usuario no ha iniciado sesión.
     * - 403 Forbidden: If the user is authenticated but does not have the 'OWNER' role or does not own the parking lot.
     *                  // Si el usuario no tiene el rol 'OWNER' o no es el dueño de este parking.
     * - 404 Not Found: If the parking lot with the specified `parkingId` is not found.
     *                  // Si no se encuentra un parking con ese ID.
     * - 500 Internal Server Error: For unexpected server errors.
     *                            // Errores internos inesperados en el servidor.
     */
    @PatchMapping(path = "/{parkingId}/availability") // Maneja solicitudes PATCH
    @PreAuthorize("hasRole('OWNER')") // Requiere rol OWNER (chequeo declarativo de Spring Security)
    public ResponseEntity<AvailabilityUpdateResponse> updateAvailability(
            @PathVariable @NotBlank String parkingId, // Validación: ID no puede ser vacío
            @Valid @RequestBody AvailabilityUpdateRequest request, // Validación del DTO en el cuerpo de la solicitud
            Authentication authentication
    ) {
        // --- Comentario en Español ---
        // 1. Obtener el ID del usuario actual (simulado por ahora).
        //    En una implementación real, esto vendría del contexto de seguridad (ej: SecurityUtils.getCurrentUserId()).
        //    IMPORTANTE: Este ID es necesario para que la capa de servicio verifique los permisos.
        String ownerId = "stub-owner-id"; // SIMULACIÓN/PLACEHOLDER para el ejemplo

        // --- Comentario en Español ---
        // 2. Llamar al método de la capa de servicio, pasando todos los datos necesarios.
        //    La lógica de negocio real está encapsulada en el servicio.
        AvailabilityUpdateResponse responseDto = parkingManagementService.updateAvailability(parkingId, request, ownerId);

        // --- Comentario en Español ---
        // 3. Devolver la respuesta HTTP exitosa con el DTO y el estado 200 OK.
        return ResponseEntity.ok(responseDto);
    }

    // --- Otros endpoints de gestión (POST /parkings, PUT /parkings/{id}...) ---
    // Podrían añadirse aquí, siguiendo una estructura similar.
}
