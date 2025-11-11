package com.igrowker.feature.parkify.features.parking.service;

import com.igrowker.feature.parkify.features.parking.dto.websocket.AvailabilityUpdateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio para enviar actualizaciones en tiempo real vía WebSocket.
 * 
 * Este servicio actúa como el "megáfono" del sistema:
 * - Cuando algo cambia en el backend (disponibilidad, nuevo parking, etc.)
 * - Este servicio envía un mensaje a TODOS los clientes conectados
 * - Los clientes actualizan su UI automáticamente
 * 
 * Arquitectura:
 * 
 *   ParkingService (cambio en DB)
 *          ↓
 *   ParkingWebSocketService (este servicio)
 *          ↓
 *   SimpMessagingTemplate (Spring)
 *          ↓
 *   WebSocket Server
 *          ↓
 *   Broadcast a todos los clientes (Frontend)
 * 
 * Canales WebSocket utilizados:
 * - /topic/parking/availability → Actualizaciones de disponibilidad
 * - /topic/parking/updates → Creación/eliminación de parkings
 * 
 * @author Parkify Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingWebSocketService {

    /**
     * Template de Spring para enviar mensajes WebSocket.
     * 
     * SimpMessagingTemplate es la herramienta que Spring proporciona
     * para enviar mensajes a través del broker STOMP configurado
     * en WebSocketConfig.
     * 
     * Métodos principales:
     * - convertAndSend(destination, payload): Envía a un canal
     * - convertAndSendToUser(user, destination, payload): Envía a un usuario específico
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Envía una actualización de disponibilidad a todos los clientes conectados.
     * 
     * Este método se llama cada vez que un dueño actualiza la disponibilidad
     * de su parking. El mensaje se envía a TODOS los usuarios que estén
     * viendo el mapa, permitiéndoles ver el cambio en tiempo real.
     * 
     * Flujo:
     * 1. Dueño actualiza disponibilidad (PATCH /my/availability)
     * 2. ParkingServiceImpl guarda el cambio en la base de datos
     * 3. ParkingServiceImpl llama a este método
     * 4. Este método crea un mensaje y lo envía al canal WebSocket
     * 5. Todos los clientes suscritos al canal reciben el mensaje
     * 6. Frontend actualiza el marcador en el mapa automáticamente
     * 
     * @param parkingId ID del parking que cambió
     * @param availableSpots Nueva cantidad de plazas disponibles
     * @param capacity Capacidad total del parking
     */
    public void broadcastAvailabilityUpdate(Long parkingId, Integer availableSpots, Integer capacity) {
        // Crear mensaje usando el método factory del DTO
        AvailabilityUpdateMessage message = AvailabilityUpdateMessage.createAvailabilityUpdate(
                parkingId, 
                availableSpots, 
                capacity
        );

        // Enviar mensaje al canal /topic/parking/availability
        // Todos los clientes suscritos a este canal recibirán el mensaje
        messagingTemplate.convertAndSend("/topic/parking/availability", message);
        
        // Log para debugging
        log.info("📡 WebSocket: Availability update broadcasted for parking {}: {} spots available (capacity: {})", 
                 parkingId, availableSpots, capacity);
    }

    /**
     * Envía una actualización de disponibilidad con información adicional del parking.
     * 
     * Versión sobrecargada que incluye el nombre del parking para
     * mostrar notificaciones más descriptivas en el frontend.
     * 
     * @param parkingId ID del parking que cambió
     * @param availableSpots Nueva cantidad de plazas disponibles
     * @param capacity Capacidad total del parking
     * @param parkingName Nombre del parking
     */
    public void broadcastAvailabilityUpdate(
            Long parkingId, 
            Integer availableSpots, 
            Integer capacity,
            String parkingName) {
        
        AvailabilityUpdateMessage message = AvailabilityUpdateMessage.createAvailabilityUpdate(
                parkingId, 
                availableSpots, 
                capacity
        );
        message.setParkingName(parkingName);

        messagingTemplate.convertAndSend("/topic/parking/availability", message);
        
        log.info("📡 WebSocket: Availability update broadcasted for parking '{}' (ID: {}): {} spots available", 
                 parkingName, parkingId, availableSpots);
    }

    /**
     * Notifica que se creó un nuevo parking.
     * 
     * Este método se llama cuando un dueño crea un nuevo parking.
     * Permite que todos los usuarios vean el nuevo parking en el mapa
     * sin recargar la página.
     * 
     * Flujo:
     * 1. Dueño crea parking (POST /my)
     * 2. ParkingServiceImpl guarda el parking
     * 3. ParkingServiceImpl llama a este método
     * 4. Frontend recibe el mensaje y agrega un nuevo marcador al mapa
     * 
     * @param parkingId ID del parking recién creado
     */
    public void broadcastParkingCreated(Long parkingId) {
        AvailabilityUpdateMessage message = AvailabilityUpdateMessage.createParkingCreated(parkingId);

        // Enviar a un canal diferente para eventos de creación/eliminación
        messagingTemplate.convertAndSend("/topic/parking/updates", message);
        
        log.info("📡 WebSocket: Parking created event broadcasted for parking {}", parkingId);
    }

    /**
     * Notifica que se eliminó un parking.
     * 
     * Este método se llama cuando un dueño elimina su parking.
     * Permite que todos los usuarios vean que el parking ya no existe
     * y lo quiten del mapa.
     * 
     * Flujo:
     * 1. Dueño elimina parking (DELETE /my)
     * 2. ParkingServiceImpl elimina el parking de la BD
     * 3. ParkingServiceImpl llama a este método
     * 4. Frontend recibe el mensaje y quita el marcador del mapa
     * 
     * @param parkingId ID del parking eliminado
     */
    public void broadcastParkingDeleted(Long parkingId) {
        AvailabilityUpdateMessage message = AvailabilityUpdateMessage.createParkingDeleted(parkingId);

        messagingTemplate.convertAndSend("/topic/parking/updates", message);
        
        log.info("📡 WebSocket: Parking deleted event broadcasted for parking {}", parkingId);
    }

    /**
     * Envía un mensaje de prueba para verificar que WebSocket funciona.
     * 
     * Útil para debugging. Puede ser llamado desde un endpoint de admin
     * para verificar que el sistema de WebSocket está funcionando correctamente.
     * 
     * @param message Mensaje de prueba
     */
    public void broadcastTestMessage(String message) {
        AvailabilityUpdateMessage testMessage = AvailabilityUpdateMessage.builder()
                .eventType("test")
                .parkingName(message)
                .build();

        messagingTemplate.convertAndSend("/topic/parking/test", testMessage);
        
        log.info("📡 WebSocket: Test message sent: {}", message);
    }
}

