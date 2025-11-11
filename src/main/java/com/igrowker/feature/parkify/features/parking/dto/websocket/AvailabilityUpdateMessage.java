package com.igrowker.feature.parkify.features.parking.dto.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Mensaje que se envía por WebSocket cuando cambia la disponibilidad de un parking.
 * 
 * Este DTO representa el mensaje JSON que se envía a todos los clientes
 * conectados cuando:
 * - Un dueño actualiza la disponibilidad de su parking
 * - Se crea un nuevo parking
 * - Se elimina un parking
 * 
 * Ejemplo de mensaje JSON:
 * {
 *   "parkingId": 1,
 *   "availableSpots": 4,
 *   "capacity": 10,
 *   "timestamp": "2025-01-15T10:30:00",
 *   "eventType": "availability_updated"
 * }
 * 
 * El frontend recibe este mensaje y actualiza la UI automáticamente
 * sin recargar la página.
 * 
 * @author Parkify Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityUpdateMessage {
    
    /**
     * ID del parking que cambió.
     * Permite al frontend identificar qué parking debe actualizar en el mapa.
     */
    private Long parkingId;
    
    /**
     * Nueva cantidad de plazas disponibles.
     * 
     * Validación en backend: debe ser >= 0 y <= capacity
     * 
     * Ejemplos:
     * - 0: Parking lleno
     * - 5: Hay 5 plazas disponibles
     * - capacity: Parking vacío (todas las plazas disponibles)
     */
    private Integer availableSpots;
    
    /**
     * Capacidad total del parking.
     * 
     * Útil para que el frontend calcule el porcentaje de ocupación:
     * ocupacion = ((capacity - availableSpots) / capacity) * 100
     * 
     * Ejemplo:
     * - capacity = 10, availableSpots = 4 → 60% ocupado
     */
    private Integer capacity;
    
    /**
     * Timestamp del momento en que se generó el evento.
     * 
     * Formato ISO 8601: "2025-01-15T10:30:00"
     * 
     * Útil para:
     * - Debugging: saber cuándo ocurrió el cambio
     * - Mostrar al usuario: "Actualizado hace 2 minutos"
     * - Ordenar eventos si llegan desordenados
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * Tipo de evento que ocurrió.
     * 
     * Valores posibles:
     * - "availability_updated": Cambió la disponibilidad
     * - "parking_created": Se creó un nuevo parking
     * - "parking_deleted": Se eliminó un parking
     * 
     * Permite al frontend decidir qué acción tomar:
     * - availability_updated: Actualizar contador del marcador
     * - parking_created: Agregar nuevo marcador al mapa
     * - parking_deleted: Quitar marcador del mapa
     */
    private String eventType;
    
    /**
     * Nombre del parking (opcional).
     * Puede ser útil para mostrar notificaciones más descriptivas.
     */
    private String parkingName;
    
    /**
     * Crea un mensaje de actualización de disponibilidad.
     * 
     * @param parkingId ID del parking
     * @param availableSpots Plazas disponibles
     * @param capacity Capacidad total
     * @return Mensaje listo para enviar por WebSocket
     */
    public static AvailabilityUpdateMessage createAvailabilityUpdate(
            Long parkingId, 
            Integer availableSpots, 
            Integer capacity) {
        return AvailabilityUpdateMessage.builder()
                .parkingId(parkingId)
                .availableSpots(availableSpots)
                .capacity(capacity)
                .timestamp(LocalDateTime.now())
                .eventType("availability_updated")
                .build();
    }
    
    /**
     * Crea un mensaje de creación de parking.
     * 
     * @param parkingId ID del nuevo parking
     * @return Mensaje listo para enviar por WebSocket
     */
    public static AvailabilityUpdateMessage createParkingCreated(Long parkingId) {
        return AvailabilityUpdateMessage.builder()
                .parkingId(parkingId)
                .timestamp(LocalDateTime.now())
                .eventType("parking_created")
                .build();
    }
    
    /**
     * Crea un mensaje de eliminación de parking.
     * 
     * @param parkingId ID del parking eliminado
     * @return Mensaje listo para enviar por WebSocket
     */
    public static AvailabilityUpdateMessage createParkingDeleted(Long parkingId) {
        return AvailabilityUpdateMessage.builder()
                .parkingId(parkingId)
                .timestamp(LocalDateTime.now())
                .eventType("parking_deleted")
                .build();
    }
}

