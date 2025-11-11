package com.igrowker.feature.parkify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket para comunicación en tiempo real.
 * 
 * Esta configuración permite que el frontend se conecte vía WebSocket
 * y reciba actualizaciones automáticas cuando cambia la disponibilidad
 * de los parkings, sin necesidad de recargar la página.
 * 
 * Componentes principales:
 * - STOMP: Protocolo simple de mensajería sobre WebSocket
 * - SockJS: Fallback a polling HTTP si WebSocket no está disponible
 * - Message Broker: Distribuye mensajes a los clientes suscritos
 * 
 * @author Parkify Team
 * @version 1.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el broker de mensajes que gestiona la distribución
     * de mensajes entre el servidor y los clientes.
     * 
     * Canales configurados:
     * - /topic: Para mensajes broadcast (1 → N clientes)
     *   Ejemplo: Actualización de disponibilidad se envía a todos
     * 
     * - /queue: Para mensajes punto a punto (1 → 1 cliente)
     *   Ejemplo: Notificación específica a un usuario
     * 
     * - /app: Prefijo para mensajes que vienen del cliente al servidor
     *   Ejemplo: Cliente envía actualización → /app/parking/update
     * 
     * @param config Registro de configuración del broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilitar broker simple en memoria para broadcasting
        // Canales de salida: los clientes se suscriben aquí
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefijo para mensajes que vienen del cliente
        // Ejemplo: cliente envía a /app/parking/update
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra el endpoint de conexión WebSocket.
     * 
     * El frontend se conectará a: ws://localhost:8080/ws
     * 
     * Características:
     * - Permite conexiones desde cualquier origen (desarrollo)
     *   En producción, cambiar a orígenes específicos
     * - Habilita SockJS como fallback para navegadores antiguos
     * - SockJS usa polling HTTP si WebSocket no funciona
     * 
     * Flujo de conexión:
     * 1. Cliente intenta WebSocket nativo
     * 2. Si falla, SockJS intenta alternativas (polling, streaming)
     * 3. Una vez conectado, establece comunicación bidireccional
     * 
     * @param registry Registro de endpoints STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // En desarrollo: permitir todas las origins
                // En producción usar:
                // .setAllowedOrigins("https://parkify-front.vercel.app", "http://localhost:5173")
                .withSockJS(); // Habilitar fallback a SockJS
    }
}

