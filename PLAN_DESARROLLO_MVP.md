# 🚀 PLAN DE DESARROLLO MVP - BACKEND PARKIFY

## 🎯 OBJETIVO
Implementar **WebSockets en tiempo real** y completar las funcionalidades faltantes para que el backend soporte completamente el MVP del frontend.

---

## 📋 FASES DEL DESARROLLO

### **FASE 1: WebSockets (CRÍTICO)** ⏱️ 4-6 horas
### **FASE 2: Mejoras y Validaciones** ⏱️ 3-4 horas  
### **FASE 3: Testing y Documentación** ⏱️ 3-4 horas

**TOTAL ESTIMADO**: 10-14 horas de desarrollo

---

## 🔴 FASE 1: IMPLEMENTACIÓN DE WEBSOCKETS (CRÍTICO)

### **¿Por qué es necesario?**
El frontend ya tiene implementado un sistema de WebSocket que:
- Se conecta a `ws://localhost:8080`
- Espera recibir eventos de disponibilidad en tiempo real
- Permite que cuando un dueño actualiza la disponibilidad, todos los conductores vean el cambio SIN recargar la página

Sin WebSocket, el MVP **NO ES FUNCIONAL**.

---

### **Paso 1.1: Agregar Dependencia de WebSocket** ⏱️ 5 min

**¿Qué hace?**  
Agrega la librería de Spring WebSocket al proyecto.

**Archivo a modificar**: `pom.xml`

```xml
<!-- Agregar después de spring-boot-starter-web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

**Comando a ejecutar**:
```bash
mvn clean install
```

---

### **Paso 1.2: Crear Configuración de WebSocket** ⏱️ 30 min

**¿Qué hace?**  
Configura el servidor WebSocket de Spring Boot para:
- Aceptar conexiones en el endpoint `/ws`
- Configurar CORS para permitir conexiones desde el frontend
- Habilitar STOMP sobre WebSocket

**Archivo a crear**: `src/main/java/com/igrowker/feature/parkify/config/WebSocketConfig.java`

```java
package com.igrowker.feature.parkify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket para comunicación en tiempo real.
 * 
 * Permite que el frontend se conecte vía WebSocket y reciba
 * actualizaciones automáticas de disponibilidad de parkings.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el broker de mensajes.
     * - /topic: Para mensajes broadcast (muchos clientes reciben)
     * - /queue: Para mensajes punto a punto (un cliente específico)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefijo para mensajes que van al broker (broadcasting)
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefijo para mensajes que vienen del cliente
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra el endpoint de conexión WebSocket.
     * El frontend se conectará a: ws://localhost:8080/ws
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Permitir todas las origins en desarrollo
                .withSockJS(); // Fallback a polling si WebSocket no está disponible
    }
}
```

**Explicación técnica**:
- **STOMP**: Protocolo simple de mensajería sobre WebSocket
- **Broker**: Intermediario que distribuye mensajes a los suscriptores
- **/topic**: Canal para broadcasting (1→N)
- **/app**: Prefijo para mensajes enviados por clientes
- **SockJS**: Librería que proporciona fallback si el navegador no soporta WebSocket

---

### **Paso 1.3: Crear DTOs para Mensajes WebSocket** ⏱️ 15 min

**¿Qué hace?**  
Define la estructura de los mensajes que se enviarán por WebSocket.

**Archivo a crear**: `src/main/java/com/igrowker/feature/parkify/features/parking/dto/websocket/AvailabilityUpdateMessage.java`

```java
package com.igrowker.feature.parkify.features.parking.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Mensaje que se envía por WebSocket cuando cambia la disponibilidad.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityUpdateMessage {
    /**
     * ID del parking que cambió
     */
    private Long parkingId;
    
    /**
     * Nueva cantidad de plazas disponibles
     */
    private Integer availableSpots;
    
    /**
     * Capacidad total del parking (para validación en frontend)
     */
    private Integer capacity;
    
    /**
     * Timestamp del cambio
     */
    private LocalDateTime timestamp;
    
    /**
     * Tipo de evento: "availability_updated", "parking_created", "parking_deleted"
     */
    private String eventType;
}
```

---

### **Paso 1.4: Crear Servicio de Broadcasting WebSocket** ⏱️ 45 min

**¿Qué hace?**  
Servicio que se encarga de enviar mensajes a todos los clientes conectados.

**Archivo a crear**: `src/main/java/com/igrowker/feature/parkify/features/parking/service/ParkingWebSocketService.java`

```java
package com.igrowker.feature.parkify.features.parking.service;

import com.igrowker.feature.parkify.features.parking.dto.websocket.AvailabilityUpdateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio para enviar actualizaciones en tiempo real vía WebSocket.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Envía una actualización de disponibilidad a todos los clientes conectados.
     * 
     * @param parkingId ID del parking
     * @param availableSpots Nueva cantidad de plazas disponibles
     * @param capacity Capacidad total del parking
     */
    public void broadcastAvailabilityUpdate(Long parkingId, Integer availableSpots, Integer capacity) {
        AvailabilityUpdateMessage message = AvailabilityUpdateMessage.builder()
                .parkingId(parkingId)
                .availableSpots(availableSpots)
                .capacity(capacity)
                .timestamp(LocalDateTime.now())
                .eventType("availability_updated")
                .build();

        // Enviar a todos los suscritos al canal /topic/parking/availability
        messagingTemplate.convertAndSend("/topic/parking/availability", message);
        
        log.info("📡 WebSocket: Availability update broadcasted for parking {}: {} spots available", 
                 parkingId, availableSpots);
    }

    /**
     * Notifica que se creó un nuevo parking.
     */
    public void broadcastParkingCreated(Long parkingId) {
        AvailabilityUpdateMessage message = AvailabilityUpdateMessage.builder()
                .parkingId(parkingId)
                .timestamp(LocalDateTime.now())
                .eventType("parking_created")
                .build();

        messagingTemplate.convertAndSend("/topic/parking/updates", message);
        log.info("📡 WebSocket: Parking created event broadcasted for parking {}", parkingId);
    }

    /**
     * Notifica que se eliminó un parking.
     */
    public void broadcastParkingDeleted(Long parkingId) {
        AvailabilityUpdateMessage message = AvailabilityUpdateMessage.builder()
                .parkingId(parkingId)
                .timestamp(LocalDateTime.now())
                .eventType("parking_deleted")
                .build();

        messagingTemplate.convertAndSend("/topic/parking/updates", message);
        log.info("📡 WebSocket: Parking deleted event broadcasted for parking {}", parkingId);
    }
}
```

**Explicación técnica**:
- **SimpMessagingTemplate**: Herramienta de Spring para enviar mensajes WebSocket
- **convertAndSend**: Convierte el objeto Java a JSON y lo envía
- **/topic/parking/availability**: Canal al que se suscriben los clientes

---

### **Paso 1.5: Integrar WebSocket en el Servicio de Parking** ⏱️ 30 min

**¿Qué hace?**  
Modifica el servicio existente para que cada vez que se actualiza la disponibilidad, también se envíe un mensaje WebSocket.

**Archivo a modificar**: `src/main/java/com/igrowker/feature/parkify/features/parking/service/ParkingServiceImpl.java`

**Agregar**:
1. Inyectar el servicio WebSocket
2. Llamarlo después de cada actualización

```java
// Al inicio de la clase, agregar:
private final ParkingWebSocketService webSocketService;

// En el método updateMyParkingAvailability, después de guardar:
@Override
public ParkingAvailabilityResponse updateMyParkingAvailability(String ownerEmail, Integer availableSpots) {
    // ... código existente ...
    
    // Guardar cambios
    Parking updatedParking = parkingRepository.save(parking);
    
    // 🔥 NUEVO: Enviar actualización por WebSocket
    webSocketService.broadcastAvailabilityUpdate(
        updatedParking.getId(),
        updatedParking.getAvailableSpots(),
        updatedParking.getCapacity()
    );
    
    // ... resto del código ...
}

// Hacer lo mismo en updateSpecificParkingAvailability

// En createMyParking, después de guardar:
webSocketService.broadcastParkingCreated(savedParking.getId());

// En deleteMyParking, después de eliminar:
webSocketService.broadcastParkingDeleted(parkingId);
```

---

### **Paso 1.6: Crear Controlador WebSocket (Opcional)** ⏱️ 20 min

**¿Qué hace?**  
Permite que el frontend también pueda ENVIAR mensajes (no solo recibir).

**Archivo a crear**: `src/main/java/com/igrowker/feature/parkify/features/parking/controller/ParkingWebSocketController.java`

```java
package com.igrowker.feature.parkify.features.parking.controller;

import com.igrowker.feature.parkify.features.parking.dto.websocket.AvailabilityUpdateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Controlador para mensajes WebSocket entrantes.
 * 
 * Los clientes pueden enviar mensajes a /app/parking/update-availability
 * y el servidor reenvía a todos los suscritos a /topic/parking/availability
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ParkingWebSocketController {

    /**
     * Recibe actualización desde un cliente y la reenvía a todos.
     * 
     * Cliente envía a: /app/parking/update-availability
     * Servidor reenvía a: /topic/parking/availability
     */
    @MessageMapping("/parking/update-availability")
    @SendTo("/topic/parking/availability")
    public AvailabilityUpdateMessage handleAvailabilityUpdate(AvailabilityUpdateMessage message) {
        log.info("📨 WebSocket: Received availability update for parking {}", message.getParkingId());
        return message;
    }
}
```

---

### **Paso 1.7: Actualizar CORS para WebSocket** ⏱️ 10 min

**¿Qué hace?**  
Asegura que el navegador permita la conexión WebSocket desde el frontend.

**Archivo a modificar**: `src/main/java/com/igrowker/feature/parkify/config/WebConfig.java`

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String frontendUrl = System.getenv("FRONTEND_URL");
                String allowedOrigin = (frontendUrl != null && !frontendUrl.isBlank()) 
                    ? frontendUrl 
                    : "http://localhost:5173";
                
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigin, "http://localhost:5173", "http://localhost:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true); // ⬅️ Importante para WebSocket
            }
        };
    }
}
```

---

### **Paso 1.8: Testing Manual de WebSocket** ⏱️ 30 min

**¿Cómo probar?**

#### **Opción A: Con el Frontend**
1. Levantar backend: `docker compose up --build`
2. Levantar frontend: `npm run dev`
3. Abrir dos navegadores:
   - Navegador 1: Login como OWNER → Cambiar disponibilidad
   - Navegador 2: Ver mapa público
4. ✅ Verificar que el mapa se actualiza automáticamente

#### **Opción B: Con Postman/Cliente WebSocket**
1. Instalar extensión "WebSocket Client" en VSCode
2. Conectar a: `ws://localhost:8080/ws`
3. Suscribirse a: `/topic/parking/availability`
4. Desde Postman, hacer `PATCH /api/v1/parkings/my/availability`
5. ✅ Verificar que llega mensaje WebSocket

#### **Opción C: Con JavaScript en la consola del navegador**
```javascript
// En la consola del navegador (F12):
const socket = new WebSocket('ws://localhost:8080/ws');

socket.onopen = () => console.log('✅ Conectado');
socket.onmessage = (event) => console.log('📨 Mensaje:', event.data);
socket.onerror = (error) => console.error('❌ Error:', error);
```

---

## 🟡 FASE 2: MEJORAS Y VALIDACIONES (IMPORTANTE)

### **Paso 2.1: Validar Disponibilidad vs Capacidad** ⏱️ 30 min

**¿Por qué?**  
Evitar que un dueño ponga 100 plazas disponibles si el parking solo tiene capacidad para 50.

**Archivo a modificar**: `ParkingServiceImpl.java`

```java
@Override
public ParkingAvailabilityResponse updateMyParkingAvailability(String ownerEmail, Integer availableSpots) {
    // ... buscar parking ...
    
    // ✅ VALIDAR: availableSpots no puede ser mayor que capacity
    if (availableSpots > parking.getCapacity()) {
        throw new InvalidAvailabilityException(
            String.format("Available spots (%d) cannot exceed capacity (%d)", 
                         availableSpots, parking.getCapacity())
        );
    }
    
    // ... resto del código ...
}
```

**Crear excepción si no existe**:
```java
package com.igrowker.feature.parkify.exception;

public class InvalidAvailabilityException extends RuntimeException {
    public InvalidAvailabilityException(String message) {
        super(message);
    }
}
```

---

### **Paso 2.2: Validar Coordenadas GPS** ⏱️ 20 min

**¿Por qué?**  
Evitar que se creen parkings con coordenadas inválidas (ej: lat=999, lon=-999).

**Archivo a modificar**: DTOs de request

```java
// En CreateMyParkingRequest, UpdateMyParkingRequest:

@NotNull(message = "Latitude cannot be null")
@Min(value = -90, message = "Latitude must be between -90 and 90")
@Max(value = 90, message = "Latitude must be between -90 and 90")
private Double latitude;

@NotNull(message = "Longitude cannot be null")
@Min(value = -180, message = "Longitude must be between -180 and 180")
@Max(value = 180, message = "Longitude must be between -180 and 180")
private Double longitude;
```

---

### **Paso 2.3: Estandarizar Mensajes de Error** ⏱️ 1 hora

**¿Por qué?**  
El frontend necesita mensajes consistentes para mostrar al usuario.

**Archivo a crear**: `src/main/java/com/igrowker/feature/parkify/common/dto/ErrorResponse.java`

```java
package com.igrowker.feature.parkify.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private List<String> details;
}
```

**Modificar**: `GlobalExceptionHandler.java` para usar este DTO consistentemente.

---

### **Paso 2.4: Agregar Índices en Base de Datos** ⏱️ 30 min

**¿Por qué?**  
Optimizar consultas geoespaciales (parkings cercanos).

**Archivo a crear**: `src/main/resources/db/migration/V2__add_indexes.sql`

```sql
-- Índice para búsquedas por dueño
CREATE INDEX idx_parking_owner_id ON parking(owner_id);

-- Índice para búsquedas por disponibilidad
CREATE INDEX idx_parking_available_spots ON parking(available_spots);

-- Índice compuesto para búsquedas geoespaciales
CREATE INDEX idx_parking_location ON parking(latitude, longitude);

-- Índice para búsquedas de usuarios por email
CREATE INDEX idx_users_email ON users(email);
```

**Nota**: Si no usas Flyway, ejecutar manualmente en PostgreSQL.

---

## 🟢 FASE 3: TESTING Y DOCUMENTACIÓN (OPCIONAL PERO RECOMENDADO)

### **Paso 3.1: Test Unitario del Servicio WebSocket** ⏱️ 1 hora

**Archivo a crear**: `src/test/java/com/igrowker/feature/parkify/features/parking/service/ParkingWebSocketServiceTest.java`

```java
@ExtendWith(MockitoExtension.class)
class ParkingWebSocketServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ParkingWebSocketService webSocketService;

    @Test
    void shouldBroadcastAvailabilityUpdate() {
        // Given
        Long parkingId = 1L;
        Integer availableSpots = 5;
        Integer capacity = 10;

        // When
        webSocketService.broadcastAvailabilityUpdate(parkingId, availableSpots, capacity);

        // Then
        verify(messagingTemplate).convertAndSend(
            eq("/topic/parking/availability"),
            argThat(message -> {
                AvailabilityUpdateMessage msg = (AvailabilityUpdateMessage) message;
                return msg.getParkingId().equals(parkingId) 
                    && msg.getAvailableSpots().equals(availableSpots);
            })
        );
    }
}
```

---

### **Paso 3.2: Test de Integración de WebSocket** ⏱️ 2 horas

**Archivo a crear**: `src/test/java/com/igrowker/feature/parkify/features/parking/WebSocketIntegrationTest.java`

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    @BeforeEach
    void setup() {
        WebSocketClient client = new StandardWebSocketClient();
        this.stompClient = new WebSocketStompClient(client);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    void shouldReceiveAvailabilityUpdate() throws Exception {
        // Implementación de test E2E con WebSocket
    }
}
```

---

### **Paso 3.3: Actualizar Documentación Swagger** ⏱️ 30 min

**Archivo a modificar**: `OpenApiConfig.java`

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Parkify API")
            .version("1.0")
            .description("""
                API REST para la aplicación Parkify.
                
                ## WebSocket
                
                Conexión WebSocket disponible en: `ws://localhost:8080/ws`
                
                ### Canales disponibles:
                - `/topic/parking/availability` - Actualizaciones de disponibilidad
                - `/topic/parking/updates` - Creación/eliminación de parkings
                
                ### Ejemplo de uso (JavaScript):
                ```javascript
                const socket = new SockJS('http://localhost:8080/ws');
                const stompClient = Stomp.over(socket);
                stompClient.connect({}, () => {
                    stompClient.subscribe('/topic/parking/availability', (message) => {
                        console.log(JSON.parse(message.body));
                    });
                });
                ```
                """)
        );
}
```

---

### **Paso 3.4: Crear README Técnico** ⏱️ 30 min

**Archivo a crear**: `WEBSOCKET_GUIDE.md`

(Documento explicando cómo funciona el WebSocket para futuros desarrolladores)

---

## 🎯 CHECKLIST FINAL

### **✅ Funcionalidad Core**
- [ ] Dependencia de WebSocket agregada
- [ ] Configuración de WebSocket creada
- [ ] DTOs de mensajes creados
- [ ] Servicio de broadcasting implementado
- [ ] Integrado en servicio de parking
- [ ] CORS configurado correctamente
- [ ] Probado con frontend (2 navegadores)

### **✅ Validaciones**
- [ ] Validar availableSpots <= capacity
- [ ] Validar coordenadas GPS (-90 a 90, -180 a 180)
- [ ] Validar formato de teléfono
- [ ] Mensajes de error estandarizados

### **✅ Optimizaciones**
- [ ] Índices en base de datos
- [ ] Logging apropiado
- [ ] Manejo de excepciones

### **✅ Testing**
- [ ] Test unitario de WebSocket service
- [ ] Test de integración E2E
- [ ] Prueba manual con frontend

### **✅ Documentación**
- [ ] Swagger actualizado
- [ ] README técnico creado
- [ ] Javadoc en métodos públicos

---

## 🚀 ORDEN DE IMPLEMENTACIÓN RECOMENDADO

### **DÍA 1 (4-6 horas)** - WebSocket Básico
1. Paso 1.1: Agregar dependencia
2. Paso 1.2: Configuración WebSocket
3. Paso 1.3: DTOs
4. Paso 1.4: Servicio de broadcasting
5. Paso 1.5: Integrar en servicio existente
6. Paso 1.7: CORS
7. Paso 1.8: Probar manualmente

**Resultado**: WebSocket funcionando end-to-end ✅

### **DÍA 2 (3-4 horas)** - Mejoras
1. Paso 2.1: Validar disponibilidad vs capacidad
2. Paso 2.2: Validar coordenadas
3. Paso 2.3: Estandarizar errores
4. Paso 2.4: Índices DB

**Resultado**: Backend robusto y validado ✅

### **DÍA 3 (opcional, 3-4 horas)** - Testing
1. Paso 3.1: Tests unitarios
2. Paso 3.2: Tests de integración
3. Paso 3.3: Documentación Swagger
4. Paso 3.4: README técnico

**Resultado**: Proyecto con calidad de producción ✅

---

## 📞 TROUBLESHOOTING

### **Problema: WebSocket no conecta**
```
❌ Error: "WebSocket handshake failed"
```
**Solución**:
1. Verificar que `spring-boot-starter-websocket` está en `pom.xml`
2. Verificar CORS en `WebSocketConfig`
3. Verificar que frontend se conecta a la URL correcta

### **Problema: Mensaje no llega a todos los clientes**
```
❌ Solo un cliente recibe el mensaje
```
**Solución**:
1. Usar `/topic` (no `/queue`)
2. Verificar que `convertAndSend` usa el canal correcto
3. Verificar que clientes están suscritos al mismo canal

### **Problema: Error de CORS**
```
❌ "Access-Control-Allow-Origin header is missing"
```
**Solución**:
1. Verificar `setAllowedOriginPatterns("*")` en `WebSocketConfig`
2. Verificar `allowCredentials(true)` en `WebConfig`

---

## 🎉 CONCLUSIÓN

Después de completar este plan, tendrás:
- ✅ WebSocket funcionando en tiempo real
- ✅ Backend completamente compatible con el frontend
- ✅ Validaciones robustas
- ✅ Código testeado y documentado
- ✅ MVP COMPLETO Y FUNCIONAL

**¡A codear! 🚀**

