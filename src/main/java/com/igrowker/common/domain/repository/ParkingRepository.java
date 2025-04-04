package com.igrowker.common.domain.repository;

import com.igrowker.common.domain.model.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
import java.util.Optional;

/**
 * Repository interface for accessing Parking entity data.
 * // --- Comentario en Español ---
 * // Interfaz del repositorio para acceder a los datos de la entidad Parking.
 */
public interface ParkingRepository extends JpaRepository<Parking, String> { // Usamos String como tipo de ID para el ejemplo

    /**
     * Finds a parking lot by its unique identifier.
     * REQUIRED for ParkingManagementService.updateAvailability and existence checks.
     * Standard JpaRepository method.
     * // --- Comentario en Español ---
     * // Busca un parking por su ID único.
     * // NECESARIO para ParkingManagementService.updateAvailability y para verificar existencia.
     * // Método estándar de JpaRepository.
     *
     * @param id The parking lot identifier.
     * @return Optional containing the parking lot if found, otherwise empty.
     */
    @Override
    // Indica explícitamente que es un método de la interfaz padre
    Optional<Parking> findById(String id);

    /**
     * Saves or updates a parking entity.
     * REQUIRED for ParkingManagementService.updateAvailability to persist changes.
     * Standard JpaRepository method.
     * // --- Comentario en Español ---
     * // Guarda o actualiza una entidad parking.
     * // NECESARIO para que ParkingManagementService.updateAvailability guarde los cambios.
     * // Método estándar de JpaRepository.
     *
     * @param parking The parking entity to save/update.
     * @return The saved/updated entity.
     */
    @Override
    // Indica explícitamente que es un método de la interfaz padre
    <S extends Parking> S save(S parking);

    /**
     * (Optional but useful) Finds a parking lot by its ID and owner ID.
     * Allows checking permissions and fetching the entity in a single DB query.
     * CAN BE USED in ParkingManagementService.updateAvailability for optimization.
     * // --- Comentario en Español ---
     * // (Opcional pero útil) Busca un parking por su ID y el ID del propietario.
     * // Permite verificar permisos y obtener la entidad en una sola consulta a la BD.
     * // PUEDE USARSE en ParkingManagementService.updateAvailability para optimizar.
     *
     * @param id      Parking ID.
     * @param ownerId Owner ID.
     * @return Optional with the parking lot if found and owned by the user.
     */
    Optional<Parking> findByIdAndOwnerId(Long id, String ownerId);

// --- Other repository methods needed for other features ---
// Page<Parking> findByOwnerId(String ownerId, Pageable pageable);
// Page<Parking> findWithinRadius(...);
}
