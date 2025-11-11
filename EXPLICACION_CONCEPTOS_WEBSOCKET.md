# 📚 EXPLICACIÓN DE CONCEPTOS - WEBSOCKETS EN PARKIFY

## 🎯 OBJETIVO DE ESTE DOCUMENTO
Explicar de forma clara y didáctica **cómo funcionan los WebSockets** y **por qué son necesarios** para el MVP de Parkify.

---

## 🤔 ¿QUÉ PROBLEMA ESTAMOS RESOLVIENDO?

### **Escenario Sin WebSocket** ❌

Imagina esta situación:

1. **María** (conductora) está viendo el mapa de parkings en su teléfono
2. Ve que el "Parking Central" tiene **5 plazas disponibles**
3. **Juan** (otro conductor) reserva una plaza
4. **Pedro** (dueño del Parking Central) actualiza manualmente: ahora quedan **4 plazas**
5. **María** sigue viendo **5 plazas** en su pantalla ❌

¿Qué pasa? María tiene información **desactualizada** y podría intentar reservar cuando ya no hay plazas.

#### **Soluciones tradicionales (malas)**:

**A) Recargar la página cada 5 segundos** 🔄
```javascript
setInterval(() => {
  window.location.reload(); // Recargar TODO
}, 5000);
```
❌ Problema: Mala experiencia de usuario, consume muchos datos, carga innecesaria al servidor.

**B) Polling - Consultar API cada X segundos** 🔄
```javascript
setInterval(() => {
  fetch('/api/parkings/1/availability')
    .then(response => response.json())
    .then(data => updateUI(data));
}, 3000);
```
❌ Problema: 100 usuarios = 100 peticiones cada 3 segundos = 2000 peticiones por minuto al servidor. Es ineficiente.

---

### **Escenario Con WebSocket** ✅

1. **María** abre la app → Se conecta al servidor vía WebSocket
2. **Juan** abre la app → Se conecta al servidor vía WebSocket  
3. **Pedro** (dueño) actualiza disponibilidad: 5 → 4 plazas
4. El **servidor** envía automáticamente el cambio a **todos** los conectados (María, Juan, etc.)
5. **María** y **Juan** ven instantáneamente: **4 plazas disponibles** ✅

#### **Ventajas**:
- ✅ **Tiempo real**: Cambios instantáneos (< 100ms)
- ✅ **Eficiente**: El servidor solo envía cuando HAY cambios
- ✅ **Escalable**: 1000 usuarios conectados, 1 mensaje enviado
- ✅ **Bidireccional**: Cliente y servidor pueden enviarse mensajes

---

## 📡 ¿QUÉ ES UN WEBSOCKET?

### **Analogía Simple**

Imagina que las comunicaciones web son como conversaciones:

#### **HTTP Normal = Llamada Telefónica Tradicional** 📞
- Cliente: "Hola servidor, ¿cuál es la disponibilidad del parking 1?"
- Servidor: "5 plazas disponibles"
- 🔴 **Se cuelga la llamada**
- (3 segundos después)
- Cliente: "Hola servidor, ¿cuál es la disponibilidad del parking 1?"
- Servidor: "5 plazas disponibles"
- 🔴 **Se cuelga la llamada**

Cada pregunta requiere:
1. Levantar el teléfono
2. Marcar
3. Esperar respuesta
4. Colgar

#### **WebSocket = Llamada Abierta** 📱
- Cliente: "Hola servidor, me quedo en línea"
- Servidor: "OK, te aviso si algo cambia"
- (10 minutos después, algo cambia)
- Servidor: "¡Hey! El parking 1 ahora tiene 4 plazas"
- Cliente: "¡Entendido, actualizo la pantalla!"
- 🟢 **La llamada sigue abierta**

Solo hay:
1. Conexión inicial
2. Mensajes cuando sea necesario
3. Conexión permanece abierta

---

## 🏗️ ARQUITECTURA DE WEBSOCKET EN PARKIFY

```
┌──────────────────┐         ┌──────────────────┐         ┌──────────────────┐
│   FRONTEND       │         │     BACKEND      │         │    POSTGRESQL    │
│   (React)        │         │  (Spring Boot)   │         │    (Database)    │
└──────────────────┘         └──────────────────┘         └──────────────────┘
        │                             │                             │
        │  1. Conectar WebSocket      │                             │
        ├────────────────────────────>│                             │
        │  ws://localhost:8080/ws     │                             │
        │                             │                             │
        │  2. ✅ Conexión establecida │                             │
        │<────────────────────────────┤                             │
        │                             │                             │
        │                             │                             │
┌───────┴──────────┐                  │                             │
│  DUEÑO actualiza │                  │                             │
│  disponibilidad  │                  │                             │
└───────┬──────────┘                  │                             │
        │                             │                             │
        │  3. PATCH /my/availability  │                             │
        ├────────────────────────────>│  4. UPDATE parking          │
        │  { availableSpots: 4 }      ├────────────────────────────>│
        │                             │  SET available_spots = 4    │
        │                             │                             │
        │  5. 200 OK                  │  6. ✅ Guardado             │
        │<────────────────────────────┤<────────────────────────────┤
        │                             │                             │
        │                             │  7. 🔥 TRIGGER WebSocket    │
        │                             │  broadcastAvailabilityUpdate()
        │                             │                             │
        │  8. 📡 WebSocket Message    │                             │
        │  (a TODOS los conectados)   │                             │
        │<────────────────────────────┤                             │
        │  {                          │                             │
        │    parkingId: 1,            │                             │
        │    availableSpots: 4        │                             │
        │  }                          │                             │
        │                             │                             │
        │  9. 🎨 Actualizar UI        │                             │
        │  (sin recargar página)      │                             │
        │                             │                             │
```

---

## 🔍 COMPONENTES TÉCNICOS EXPLICADOS

### **1. SockJS**

**¿Qué es?**  
Librería que proporciona un "plan B" si el navegador no soporta WebSocket nativo.

**Analogía**:  
Si tu teléfono moderno no funciona, SockJS automáticamente cambia a un teléfono fijo antiguo (polling HTTP).

**Ejemplo**:
```java
.withSockJS(); // Fallback automático
```

---

### **2. STOMP (Simple Text Oriented Messaging Protocol)**

**¿Qué es?**  
Protocolo simple para enviar mensajes sobre WebSocket. Es como el "idioma" que usan el frontend y backend para comunicarse.

**Analogía**:  
WebSocket es el teléfono, STOMP es el idioma que hablas por teléfono (español, inglés, etc.).

**Estructura de un mensaje STOMP**:
```
SEND
destination:/app/parking/update-availability
content-type:application/json

{"parkingId":1,"availableSpots":4}
```

---

### **3. Message Broker (Broker de Mensajes)**

**¿Qué es?**  
Intermediario que recibe mensajes y los distribuye a los interesados.

**Analogía**:  
Es como un portero de un edificio:
- Recibe paquetes (mensajes)
- Sabe qué vecinos (clientes) quieren qué tipo de paquetes
- Entrega cada paquete a los vecinos correctos

**Configuración**:
```java
config.enableSimpleBroker("/topic", "/queue");
```

- `/topic`: Para mensajes "broadcast" (todos los suscritos reciben)
- `/queue`: Para mensajes "punto a punto" (solo un destinatario)

---

### **4. Canales (Destinations)**

**¿Qué son?**  
"Direcciones" a las que los clientes se suscriben para recibir mensajes.

**Analogía**:  
Como canales de TV: te suscribes al canal que te interesa.

#### **Canales en Parkify**:

**`/topic/parking/availability`** 📢
- **Tipo**: Broadcast (1 → N)
- **Uso**: Actualizaciones de disponibilidad
- **Quién recibe**: Todos los usuarios viendo el mapa

**`/topic/parking/updates`** 📢
- **Tipo**: Broadcast (1 → N)
- **Uso**: Crear/eliminar parkings
- **Quién recibe**: Todos los usuarios

**`/app/parking/update-availability`** 📨
- **Tipo**: Cliente → Servidor
- **Uso**: Cliente envía actualización
- **Procesamiento**: Servidor la valida y reenvía

---

### **5. SimpMessagingTemplate**

**¿Qué es?**  
Herramienta de Spring para enviar mensajes WebSocket desde el código Java.

**Analogía**:  
Es como un "megáfono" que el servidor usa para hablar a todos los conectados.

**Código**:
```java
@Service
public class ParkingWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    
    public void broadcastAvailabilityUpdate(Long parkingId, Integer availableSpots) {
        AvailabilityUpdateMessage message = new AvailabilityUpdateMessage(parkingId, availableSpots);
        
        // 📢 Enviar a todos los suscritos a este canal
        messagingTemplate.convertAndSend("/topic/parking/availability", message);
    }
}
```

**¿Qué hace `convertAndSend`?**
1. Convierte el objeto Java → JSON
2. Envía el JSON a todos los suscritos al canal

---

## 🎬 FLUJO COMPLETO PASO A PASO

### **Flujo 1: Conexión Inicial**

#### **Frontend (React)**:
```javascript
// 1. Crear conexión WebSocket
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

// 2. Conectar al servidor
stompClient.connect({}, () => {
  console.log('✅ Conectado al servidor');
  
  // 3. Suscribirse al canal de actualizaciones
  stompClient.subscribe('/topic/parking/availability', (message) => {
    const data = JSON.parse(message.body);
    console.log('📨 Recibido:', data);
    
    // 4. Actualizar la UI
    updateParkingAvailability(data.parkingId, data.availableSpots);
  });
});
```

#### **Backend (Spring Boot)**:
```java
// WebSocketConfig.java

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    // 1. Configurar el broker de mensajes
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // Canales de salida
        config.setApplicationDestinationPrefixes("/app"); // Canales de entrada
    }
    
    // 2. Configurar el endpoint de conexión
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 👈 URL de conexión
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

---

### **Flujo 2: Dueño Actualiza Disponibilidad**

#### **1. Frontend envía petición HTTP**:
```javascript
// Panel del dueño
async function updateAvailability(parkingId, availableSpots) {
  const response = await fetch(`/api/v1/parkings/${parkingId}/availability`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ availableSpots })
  });
  
  if (response.ok) {
    console.log('✅ Disponibilidad actualizada');
  }
}
```

#### **2. Backend procesa la petición**:
```java
// ParkingController.java

@PatchMapping("/{parkingId}/availability")
public ResponseEntity<ParkingAvailabilityResponse> updateAvailability(
    @PathVariable Long parkingId,
    @RequestBody UpdateAvailabilityRequest request
) {
    // 1. Validar y guardar en DB
    ParkingAvailabilityResponse response = parkingService.updateAvailability(parkingId, request);
    
    // 2. ✅ Responder al dueño (HTTP)
    return ResponseEntity.ok(response);
}
```

#### **3. Servicio actualiza DB y envía WebSocket**:
```java
// ParkingServiceImpl.java

@Service
public class ParkingServiceImpl implements ParkingService {
    
    private final ParkingRepository parkingRepository;
    private final ParkingWebSocketService webSocketService; // 👈 Inyectar
    
    public ParkingAvailabilityResponse updateAvailability(Long parkingId, UpdateAvailabilityRequest request) {
        // 1. Buscar parking en DB
        Parking parking = parkingRepository.findById(parkingId)
            .orElseThrow(() -> new ParkingNotFoundException(parkingId));
        
        // 2. Validar
        if (request.getAvailableSpots() > parking.getCapacity()) {
            throw new InvalidAvailabilityException("Exceeds capacity");
        }
        
        // 3. Actualizar
        parking.setAvailableSpots(request.getAvailableSpots());
        Parking saved = parkingRepository.save(parking);
        
        // 4. 🔥 ENVIAR WEBSOCKET A TODOS
        webSocketService.broadcastAvailabilityUpdate(
            saved.getId(),
            saved.getAvailableSpots(),
            saved.getCapacity()
        );
        
        // 5. Retornar respuesta HTTP
        return mapToResponse(saved);
    }
}
```

#### **4. Servicio WebSocket envía mensaje**:
```java
// ParkingWebSocketService.java

@Service
public class ParkingWebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public void broadcastAvailabilityUpdate(Long parkingId, Integer availableSpots, Integer capacity) {
        // 1. Crear mensaje
        AvailabilityUpdateMessage message = AvailabilityUpdateMessage.builder()
            .parkingId(parkingId)
            .availableSpots(availableSpots)
            .capacity(capacity)
            .timestamp(LocalDateTime.now())
            .eventType("availability_updated")
            .build();
        
        // 2. 📢 Enviar a TODOS los conectados al canal
        messagingTemplate.convertAndSend("/topic/parking/availability", message);
        
        log.info("📡 Broadcasted update for parking {}: {} spots", parkingId, availableSpots);
    }
}
```

#### **5. Todos los frontends conectados reciben el mensaje**:
```javascript
// En TODOS los navegadores conectados se ejecuta:
stompClient.subscribe('/topic/parking/availability', (message) => {
  const data = JSON.parse(message.body);
  // {
  //   parkingId: 1,
  //   availableSpots: 4,
  //   capacity: 10,
  //   timestamp: "2025-01-15T10:30:00",
  //   eventType: "availability_updated"
  // }
  
  // Actualizar la UI automáticamente
  const marker = map.getMarkerById(data.parkingId);
  marker.setAvailableSpots(data.availableSpots); // 🎨 Actualizar marcador
  
  console.log(`✨ Parking ${data.parkingId} ahora tiene ${data.availableSpots} plazas`);
});
```

---

## 🔒 SEGURIDAD Y CORS

### **¿Por qué necesitamos configurar CORS?**

**CORS** (Cross-Origin Resource Sharing) es una política de seguridad del navegador.

**Problema**:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- **Diferentes orígenes** → Navegador bloquea por defecto

**Solución**:
```java
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 👈 Permitir cualquier origen
                .withSockJS();
    }
}
```

**Para producción** (más seguro):
```java
.setAllowedOriginPatterns("https://parkify-front.vercel.app")
```

---

## 📊 COMPARATIVA: HTTP vs WebSocket

| Característica | HTTP REST | WebSocket |
|---------------|-----------|-----------|
| **Conexión** | Abrir y cerrar por cada petición | Una vez, permanece abierta |
| **Dirección** | Cliente → Servidor (unidireccional) | Bidireccional |
| **Latencia** | ~100-500ms por petición | ~10-50ms |
| **Overhead** | Headers grandes en cada petición | Headers solo en handshake inicial |
| **Uso de datos** | Alto (polling continuo) | Bajo (solo cuando hay cambios) |
| **Escalabilidad** | Limitada (muchas conexiones cortas) | Alta (pocas conexiones largas) |
| **Casos de uso** | CRUD, cargar datos | Tiempo real, chat, notificaciones |

---

## 🎯 CUÁNDO USAR WEBSOCKET vs HTTP

### **Usar HTTP REST** 📄
- ✅ Cargar página inicial
- ✅ Login/Register
- ✅ CRUD de parkings (crear, editar, eliminar)
- ✅ Consultas puntuales

### **Usar WebSocket** 📡
- ✅ Actualizaciones de disponibilidad en tiempo real
- ✅ Notificaciones push
- ✅ Chat en vivo
- ✅ Dashboard con métricas en vivo

### **En Parkify**:
- **HTTP**: Autenticación, crear parking, buscar parkings
- **WebSocket**: Actualizar disponibilidad en tiempo real

---

## 🐛 DEBUGGING Y TROUBLESHOOTING

### **Cómo verificar que WebSocket funciona**

#### **1. En el navegador (Chrome DevTools)**
1. Abrir DevTools (F12)
2. Ir a la pestaña **Network**
3. Filtrar por **WS** (WebSocket)
4. Buscar conexión a `ws://localhost:8080/ws`
5. ✅ Si aparece, está conectado
6. Click en la conexión → Ver mensajes enviados/recibidos

#### **2. En el backend (Logs)**
```
📡 WebSocket: Availability update broadcasted for parking 1: 4 spots available
```

#### **3. Errores comunes**

**Error**: `WebSocket handshake failed`
```
❌ Probable causa: CORS no configurado correctamente
✅ Solución: Verificar .setAllowedOriginPatterns("*")
```

**Error**: `Connection refused`
```
❌ Probable causa: Backend no está corriendo
✅ Solución: docker compose up
```

**Error**: `Message not received`
```
❌ Probable causa: Canal incorrecto
✅ Solución: Verificar que cliente se suscribe al mismo canal que el servidor envía
```

---

## 🎓 RESUMEN PARA EXPLICAR A OTROS

### **Explicación Breve** (30 segundos)
> "WebSocket es una conexión permanente entre el navegador y el servidor. En vez de preguntar cada 5 segundos 'ey ¿hay cambios?', el servidor directamente te avisa cuando algo cambia. Es como tener un WhatsApp abierto en vez de enviar emails constantemente."

### **Explicación Técnica** (2 minutos)
> "Implementamos WebSocket con Spring Boot y STOMP protocol. Cuando un dueño actualiza la disponibilidad, el backend guarda el cambio en PostgreSQL y luego usa SimpMessagingTemplate para hacer broadcast del evento a todos los clientes suscritos al canal /topic/parking/availability. El frontend (React con Socket.IO) recibe el mensaje en tiempo real y actualiza la UI sin recargar la página. Esto reduce latencia de ~5 segundos (polling) a ~50ms (WebSocket) y disminuye carga del servidor de 100 peticiones/segundo a 1 broadcast por cambio real."

---

## 🚀 PRÓXIMOS PASOS

Ahora que entiendes los conceptos, puedes:
1. Leer el **PLAN_DESARROLLO_MVP.md** para la implementación paso a paso
2. Comenzar con la Fase 1: WebSocket básico
3. Probar con el frontend existente
4. Iterar y mejorar

**¡Éxito con la implementación! 🎉**

