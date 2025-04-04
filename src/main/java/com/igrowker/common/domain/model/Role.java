package com.igrowker.common.domain.model;

/**
 * Defines the user roles within the Parkify application.
 * // --- Comentario en Español ---
 * // Define los roles de usuario dentro de la aplicación Parkify.
 */
public enum Role {
    /**
     * Represents a parking lot owner who can manage their parking spaces.
     * // --- Comentario en Español ---
     * // Representa a un propietario de parking que puede gestionar sus plazas.
     */
    OWNER,

    /**
     * Represents a driver using the application to find parking.
     * (May not be used if drivers are anonymous in MVP).
     * // --- Comentario en Español ---
     * // Representa a un conductor que usa la aplicación para buscar parking.
     * // (Puede no usarse si los conductores son anónimos en el MVP).
     */
    DRIVER,

    /**
     * Represents a general user (can be used instead of DRIVER or for future roles).
     * // --- Comentario en Español ---
     * // Representa a un usuario general (puede usarse en lugar de DRIVER o para futuros roles).
     */
    USER,

    /**
     * Represents an administrator with elevated privileges.
     * // --- Comentario en Español ---
     * // Representa a un administrador con privilegios elevados.
     */
    ADMIN
    // Add other roles as needed
    // --- Comentario en Español ---
    // Añadir otros roles según sea necesario
}