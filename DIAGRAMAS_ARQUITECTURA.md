# 🎨 DIAGRAMAS DE ARQUITECTURA - PARKIFY BACKEND

## 📋 ÍNDICE
1. [Arquitectura General](#arquitectura-general)
2. [Flujo de WebSocket](#flujo-de-websocket)
3. [Estructura de Proyecto](#estructura-de-proyecto)
4. [Modelo de Datos](#modelo-de-datos)
5. [Flujo de Autenticación](#flujo-de-autenticación)
6. [Flujo de Actualización de Disponibilidad](#flujo-de-actualización-de-disponibilidad)

---

## 🏗️ ARQUITECTURA GENERAL

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         FRONTEND (React + Vite)                         │
│                        http://localhost:5173                            │
│                                                                         │
│  ┌───────────────┐  ┌──────────────┐  ┌────────────────┐             │
│  │  Public Map   │  │ Owner Panel  │  │ Driver Panel   │             │
│  │  (MapPage)    │  │ (Dashboard)  │  │ (Reservations) │             │
│  └───────┬───────┘  └──────┬───────┘  └────────┬───────┘             │
│          │                 │                    │                      │
└──────────┼─────────────────┼────────────────────┼──────────────────────┘
           │                 │                    │
           │ HTTP REST       │ HTTP REST          │ HTTP REST
           │ + WebSocket     │ + WebSocket        │
           │                 │                    │
           ▼                 ▼                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      SPRING BOOT BACKEND                                 │
│                     http://localhost:8080                                │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                     CONTROLLERS LAYER                            │  │
│  │  ┌────────────┐  ┌────────────┐  ┌──────────────┐             │  │
│  │  │   Auth     │  │  Parking   │  │   Booking    │             │  │
│  │  │ Controller │  │ Controller │  │  Controller  │             │  │
│  │  └─────┬──────┘  └─────┬──────┘  └──────┬───────┘             │  │
│  └────────┼───────────────┼─────────────────┼─────────────────────┘  │
│           │               │                 │                          │
│  ┌────────┼───────────────┼─────────────────┼─────────────────────┐  │
│  │        │    SERVICES LAYER               │                     │  │
│  │        ▼               ▼                 ▼                     │  │
│  │  ┌──────────┐   ┌──────────┐      ┌──────────┐               │  │
│  │  │   Auth   │   │ Parking  │      │ Booking  │               │  │
│  │  │ Service  │   │ Service  │      │ Service  │               │  │
│  │  └──────────┘   └────┬─────┘      └──────────┘               │  │
│  │                      │                                         │  │
│  │                      │ calls                                   │  │
│  │                      ▼                                         │  │
│  │              ┌──────────────────┐                             │  │
│  │              │  WebSocket       │ 🔥 NUEVO                    │  │
│  │              │  Service         │                             │  │
│  │              └────────┬─────────┘                             │  │
│  └───────────────────────┼───────────────────────────────────────┘  │
│                          │                                            │
│  ┌───────────────────────┼───────────────────────────────────────┐  │
│  │  REPOSITORIES LAYER   │                                       │  │
│  │                       ▼                                       │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐       │  │
│  │  │   User       │  │   Parking    │  │   Booking   │       │  │
│  │  │ Repository   │  │  Repository  │  │ Repository  │       │  │
│  │  └──────┬───────┘  └──────┬───────┘  └──────┬──────┘       │  │
│  └─────────┼──────────────────┼──────────────────┼──────────────┘  │
│            │                  │                  │                   │
│  ┌─────────┼──────────────────┼──────────────────┼──────────────┐  │
│  │  WEBSOCKET LAYER          │                  │              │  │
│  │                            │                  │              │  │
│  │  ┌─────────────────────────┴──────────────────┴─────────┐  │  │
│  │  │  WebSocket Config (STOMP over WebSocket)            │  │  │
│  │  │  - Endpoint: /ws                                     │  │  │
│  │  │  - Broker: /topic, /queue                           │  │  │
│  │  │  - App prefix: /app                                  │  │  │
│  │  └──────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────────────────────────────────┘  │
│            │                  │                  │                   │
└────────────┼──────────────────┼──────────────────┼───────────────────┘
             │                  │                  │
             ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      POSTGRESQL DATABASE                                 │
│                     localhost:5432 / db_parkify                          │
│                                                                          │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌──────────────────┐         │
│  │  users  │  │ parking │  │ booking │  │ occupancy_history│         │
│  └─────────┘  └─────────┘  └─────────┘  └──────────────────┘         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 📡 FLUJO DE WEBSOCKET (TIEMPO REAL)

### **Flujo Completo de Actualización de Disponibilidad**

```
┌──────────────┐          ┌──────────────┐          ┌──────────────┐
│   DUEÑO      │          │   BACKEND    │          │  CONDUCTOR   │
│  (Owner)     │          │ Spring Boot  │          │  (Driver)    │
└──────┬───────┘          └──────┬───────┘          └──────┬───────┘
       │                         │                         │
       │  1. Conectar WebSocket  │                         │
       ├────────────────────────>│                         │
       │  ws://localhost:8080/ws │                         │
       │                         │                         │
       │  ✅ Connected           │                         │
       │<────────────────────────┤                         │
       │                         │                         │
       │                         │  2. Conectar WebSocket  │
       │                         │<────────────────────────┤
       │                         │  ws://localhost:8080/ws │
       │                         │                         │
       │                         │  ✅ Connected           │
       │                         ├────────────────────────>│
       │                         │                         │
       │                         │  3. Subscribe to channel│
       │                         │<────────────────────────┤
       │                         │  /topic/parking/        │
       │                         │   availability          │
       │                         │                         │
       │  4. Update Availability │                         │
       │  PATCH /my/availability │                         │
       ├────────────────────────>│                         │
       │  { availableSpots: 4 }  │                         │
       │                         │                         │
       │                         │  5. Validate & Save     │
       │                         │  ┌──────────────────┐   │
       │                         │  │  if (spots <=    │   │
       │                         │  │     capacity) {  │   │
       │                         │  │    save()        │   │
       │                         │  │  }              │   │
       │                         │  └──────────────────┘   │
       │                         │                         │
       │  6. 200 OK              │                         │
       │<────────────────────────┤                         │
       │  { availableSpots: 4 }  │                         │
       │                         │                         │
       │                         │  7. 🔥 BROADCAST        │
       │                         │  webSocketService       │
       │                         │   .broadcast()          │
       │                         │  ┌──────────────────┐   │
       │                         │  │ Send to ALL      │   │
       │                         │  │ subscribers      │   │
       │                         │  └──────────────────┘   │
       │                         │                         │
       │  8. 📨 WS Message       │  9. 📨 WS Message       │
       │<────────────────────────┼────────────────────────>│
       │  {                      │  {                      │
       │    parkingId: 1,        │    parkingId: 1,        │
       │    availableSpots: 4,   │    availableSpots: 4,   │
       │    capacity: 10,        │    capacity: 10,        │
       │    timestamp: "..."     │    timestamp: "..."     │
       │  }                      │  }                      │
       │                         │                         │
       │  10. Update UI          │  11. Update UI          │
       │  ✨ Show confirmation   │  ✨ Update marker       │
       │                         │  ✨ Update counter      │
       │                         │  (NO page reload!)      │
       │                         │                         │
```

### **Canales WebSocket**

```
┌─────────────────────────────────────────────────────────────────┐
│                    WEBSOCKET CHANNELS                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  /topic/parking/availability                                   │
│  ├─ Type: BROADCAST (1 → N)                                   │
│  ├─ Purpose: Real-time availability updates                    │
│  ├─ Subscribers: All users viewing the map                     │
│  └─ Message: { parkingId, availableSpots, capacity, timestamp }│
│                                                                 │
│  /topic/parking/updates                                        │
│  ├─ Type: BROADCAST (1 → N)                                   │
│  ├─ Purpose: Parking CRUD events                              │
│  ├─ Subscribers: All users                                     │
│  └─ Events: parking_created, parking_deleted                   │
│                                                                 │
│  /app/parking/update-availability (optional)                   │
│  ├─ Type: CLIENT → SERVER                                      │
│  ├─ Purpose: Client can send updates directly via WS          │
│  └─ Handler: ParkingWebSocketController                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📂 ESTRUCTURA DE PROYECTO

```
parkify-back/
│
├── src/
│   ├── main/
│   │   ├── java/com/igrowker/feature/parkify/
│   │   │   │
│   │   │   ├── ParkifyApplication.java           # Main class
│   │   │   │
│   │   │   ├── common/                            # Utilidades compartidas
│   │   │   │   ├── dto/
│   │   │   │   └── service/
│   │   │   │
│   │   │   ├── config/                            # Configuraciones globales
│   │   │   │   ├── OpenApiConfig.java            # Swagger
│   │   │   │   ├── WebConfig.java                # CORS HTTP
│   │   │   │   └── WebSocketConfig.java          # 🔥 NUEVO - WebSocket
│   │   │   │
│   │   │   ├── exception/                         # Manejo de errores
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ParkingNotFoundException.java
│   │   │   │   └── InvalidAvailabilityException.java
│   │   │   │
│   │   │   └── features/                          # Módulos funcionales
│   │   │       │
│   │   │       ├── auth/                          # ✅ Autenticación
│   │   │       │   ├── controller/
│   │   │       │   │   └── AuthController.java
│   │   │       │   ├── dto/
│   │   │       │   ├── entities/
│   │   │       │   │   ├── AuthUser.java
│   │   │       │   │   └── Role.java (OWNER, DRIVER)
│   │   │       │   ├── repository/
│   │   │       │   ├── security/
│   │   │       │   │   ├── JwtService.java
│   │   │       │   │   ├── SecurityConfig.java
│   │   │       │   │   └── AuthTokenFilter.java
│   │   │       │   └── service/
│   │   │       │
│   │   │       ├── parking/                       # ✅ Parkings (CORE)
│   │   │       │   ├── controller/
│   │   │       │   │   ├── ParkingController.java
│   │   │       │   │   └── ParkingWebSocketController.java  # 🔥 NUEVO
│   │   │       │   ├── dto/
│   │   │       │   │   ├── request/
│   │   │       │   │   ├── response/
│   │   │       │   │   └── websocket/             # 🔥 NUEVO
│   │   │       │   │       └── AvailabilityUpdateMessage.java
│   │   │       │   ├── entities/
│   │   │       │   │   └── Parking.java
│   │   │       │   ├── repository/
│   │   │       │   │   └── ParkingRepository.java
│   │   │       │   └── service/
│   │   │       │       ├── ParkingService.java
│   │   │       │       ├── ParkingServiceImpl.java (🔥 MODIFICAR)
│   │   │       │       └── ParkingWebSocketService.java  # 🔥 NUEVO
│   │   │       │
│   │   │       ├── booking/                       # ✅ Reservas
│   │   │       ├── recommendation/                # ✅ Recomendaciones
│   │   │       ├── user/                          # ✅ Usuarios
│   │   │       ├── config/                        # ✅ Config de app
│   │   │       ├── content/                       # ✅ Contenido
│   │   │       └── operation/                     # ✅ Estado operaciones
│   │   │
│   │   └── resources/
│   │       ├── application.properties             # Config principal
│   │       ├── application-dev.properties         # Config desarrollo
│   │       └── db/migration/                      # Migraciones SQL
│   │
│   └── test/
│       └── java/...                               # Tests unitarios
│
├── pom.xml                                         # Dependencias Maven
├── Dockerfile                                      # Imagen Docker
├── docker-compose.yml                              # Orquestación
└── README.md                                       # Documentación
```

---

## 🗄️ MODELO DE DATOS

```
┌─────────────────────────────────────────────────────────────────┐
│                           users                                 │
├─────────────────────────────────────────────────────────────────┤
│ PK  id              BIGINT                                      │
│     username        VARCHAR                                     │
│ UQ  email           VARCHAR                                     │
│     password        VARCHAR (hashed)                            │
│     role            ENUM('OWNER', 'DRIVER')                     │
│     contact_phone   VARCHAR                                     │
│     latitude        DOUBLE                                      │
│     longitude       DOUBLE                                      │
│     created_at      TIMESTAMP                                   │
│     updated_at      TIMESTAMP                                   │
└─────────────────────────────────────────────────────────────────┘
                               │
                               │ 1:N
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                          parking                                │
├─────────────────────────────────────────────────────────────────┤
│ PK  id                  BIGINT                                  │
│     name                VARCHAR                                 │
│     address             VARCHAR                                 │
│     latitude            DOUBLE                                  │
│     longitude           DOUBLE                                  │
│     description         TEXT                                    │
│     capacity            INTEGER  (Total plazas)                 │
│     available_spots     INTEGER  (Plazas disponibles) 🔥       │
│     hourly_rate         DOUBLE                                  │
│     working_hours       VARCHAR                                 │
│ FK  owner_id            BIGINT → users.id                       │
│     parking_phone       VARCHAR                                 │
│     parking_image_url   VARCHAR                                 │
└─────────────────────────────────────────────────────────────────┘
                               │
                               │ 1:N
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                          booking                                │
├─────────────────────────────────────────────────────────────────┤
│ PK  id              BIGINT                                      │
│ FK  user_id         BIGINT → users.id                           │
│ FK  parking_id      BIGINT → parking.id                         │
│     start_time      TIMESTAMP                                   │
│     end_time        TIMESTAMP                                   │
│     status          VARCHAR                                     │
│     created_at      TIMESTAMP                                   │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    occupancy_history                            │
├─────────────────────────────────────────────────────────────────┤
│ PK  id                  BIGINT                                  │
│ FK  parking_id          BIGINT → parking.id                     │
│     timestamp           TIMESTAMP                               │
│     occupied_spots      INTEGER                                 │
│     available_spots     INTEGER                                 │
└─────────────────────────────────────────────────────────────────┘
```

### **Índices Recomendados**

```sql
-- Búsquedas por dueño
CREATE INDEX idx_parking_owner_id ON parking(owner_id);

-- Búsquedas por disponibilidad
CREATE INDEX idx_parking_available_spots ON parking(available_spots);

-- Búsquedas geoespaciales
CREATE INDEX idx_parking_location ON parking(latitude, longitude);

-- Búsquedas de usuarios por email
CREATE INDEX idx_users_email ON users(email);

-- Búsquedas de reservas por usuario
CREATE INDEX idx_booking_user_id ON booking(user_id);

-- Búsquedas de reservas por parking
CREATE INDEX idx_booking_parking_id ON booking(parking_id);
```

---

## 🔐 FLUJO DE AUTENTICACIÓN

```
┌──────────────┐                    ┌──────────────┐
│   FRONTEND   │                    │   BACKEND    │
│              │                    │              │
└──────┬───────┘                    └──────┬───────┘
       │                                   │
       │  1. POST /api/v1/auth/register   │
       ├──────────────────────────────────>│
       │  {                                │
       │    email: "owner@example.com",    │
       │    password: "secret123",         │
       │    role: "OWNER",                 │
       │    contactPhone: "+123456789"     │
       │  }                                │
       │                                   │
       │                                   │  2. Hash password
       │                                   │  BCryptPasswordEncoder
       │                                   │
       │                                   │  3. Save to DB
       │                                   │  UserRepository.save()
       │                                   │
       │  4. 201 Created                   │
       │<──────────────────────────────────┤
       │  {                                │
       │    id: 1,                         │
       │    email: "owner@example.com",    │
       │    role: "OWNER"                  │
       │  }                                │
       │                                   │
       │  5. POST /api/v1/auth/login       │
       ├──────────────────────────────────>│
       │  {                                │
       │    email: "owner@example.com",    │
       │    password: "secret123"          │
       │  }                                │
       │                                   │
       │                                   │  6. Validate password
       │                                   │  passwordEncoder.matches()
       │                                   │
       │                                   │  7. Generate JWT
       │                                   │  JwtService.generateToken()
       │                                   │
       │  8. 200 OK                        │
       │<──────────────────────────────────┤
       │  {                                │
       │    token: "eyJhbGciOiJIUzI1...",  │
       │    type: "Bearer",                │
       │    expiresIn: 36000,              │
       │    user: {                        │
       │      id: 1,                       │
       │      email: "owner@example.com",  │
       │      role: "OWNER"                │
       │    }                              │
       │  }                                │
       │                                   │
       │  9. Store token in localStorage   │
       │  localStorage.setItem('token')    │
       │                                   │
       │  10. Subsequent requests          │
       │  GET /api/v1/parkings/my          │
       ├──────────────────────────────────>│
       │  Headers:                         │
       │    Authorization: Bearer eyJ...   │
       │                                   │
       │                                   │  11. Validate JWT
       │                                   │  AuthTokenFilter
       │                                   │  JwtService.validateToken()
       │                                   │
       │                                   │  12. Extract user from token
       │                                   │  SecurityContextHolder.setAuth()
       │                                   │
       │  13. 200 OK + Data                │
       │<──────────────────────────────────┤
       │                                   │
```

---

## 🔄 FLUJO COMPLETO: ACTUALIZACIÓN DE DISPONIBILIDAD

```
┌────────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────┐
│  Frontend  │    │  Controller  │    │   Service    │    │ Database │
│  (Owner)   │    │              │    │              │    │          │
└─────┬──────┘    └──────┬───────┘    └──────┬───────┘    └────┬─────┘
      │                  │                   │                  │
      │  1. Click "Save" │                   │                  │
      │  availableSpots=4│                   │                  │
      │                  │                   │                  │
      │  2. PATCH /my/   │                   │                  │
      │     availability │                   │                  │
      ├─────────────────>│                   │                  │
      │  + JWT Token     │                   │                  │
      │                  │                   │                  │
      │                  │  3. Validate JWT  │                  │
      │                  │  Extract user     │                  │
      │                  │                   │                  │
      │                  │  4. Call service  │                  │
      │                  ├──────────────────>│                  │
      │                  │  (ownerEmail, 4)  │                  │
      │                  │                   │                  │
      │                  │                   │  5. Find owner   │
      │                  │                   ├─────────────────>│
      │                  │                   │  by email        │
      │                  │                   │<─────────────────┤
      │                  │                   │  Owner found     │
      │                  │                   │                  │
      │                  │                   │  6. Find parking │
      │                  │                   ├─────────────────>│
      │                  │                   │  by owner_id     │
      │                  │                   │<─────────────────┤
      │                  │                   │  Parking found   │
      │                  │                   │                  │
      │                  │                   │  7. Validate     │
      │                  │                   │  ┌────────────┐  │
      │                  │                   │  │ if (4 <= 10│  │
      │                  │                   │  │  capacity) │  │
      │                  │                   │  │   ✅ OK    │  │
      │                  │                   │  └────────────┘  │
      │                  │                   │                  │
      │                  │                   │  8. Update       │
      │                  │                   ├─────────────────>│
      │                  │                   │  SET available_  │
      │                  │                   │  spots = 4       │
      │                  │                   │<─────────────────┤
      │                  │                   │  ✅ Updated      │
      │                  │                   │                  │
      │                  │                   │  9. 🔥 WebSocket │
      │                  │                   │  Broadcast       │
      │                  │                   │  ┌────────────┐  │
      │                  │                   │  │ Send to    │  │
      │                  │                   │  │ ALL clients│  │
      │                  │                   │  └────────────┘  │
      │                  │                   │                  │
      │                  │  10. Return DTO   │                  │
      │                  │<──────────────────┤                  │
      │                  │                   │                  │
      │  11. 200 OK      │                   │                  │
      │<─────────────────┤                   │                  │
      │  { parkingId: 1, │                   │                  │
      │    availableSpots│                   │                  │
      │    : 4 }         │                   │                  │
      │                  │                   │                  │
      │  12. ✨ Show     │                   │                  │
      │  success toast   │                   │                  │
      │                  │                   │                  │
      │                  │                   │                  │
      │  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
      │                                                          │
      │  13. 📡 WebSocket Message (to ALL clients)              │
      │<─────────────────────────────────────────────────────────┤
      │  {                                                       │
      │    parkingId: 1,                                         │
      │    availableSpots: 4,                                    │
      │    capacity: 10,                                         │
      │    timestamp: "2025-01-15T10:30:00",                    │
      │    eventType: "availability_updated"                     │
      │  }                                                       │
      │                                                          │
      │  14. ✨ Update UI automatically                          │
      │  - Update marker on map                                  │
      │  - Update counter                                        │
      │  - Update recommendations                                │
      │  (NO page reload!)                                       │
      │                                                          │
```

---

## 🛡️ CAPAS DE SEGURIDAD

```
┌─────────────────────────────────────────────────────────────────┐
│                       REQUEST FROM FRONTEND                      │
└────────────────────────────────┬────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   1. CORS Filter (WebConfig)                     │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  ✅ Check if origin is allowed                            │ │
│  │  ✅ Check if method is allowed (GET, POST, etc.)          │ │
│  │  ✅ Check if headers are allowed                          │ │
│  └────────────────────────────────────────────────────────────┘ │
└────────────────────────────────┬────────────────────────────────┘
                                 │ ✅ CORS OK
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│              2. Authentication Filter (AuthTokenFilter)          │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  ✅ Extract JWT from "Authorization: Bearer ..." header   │ │
│  │  ✅ Validate JWT signature                                │ │
│  │  ✅ Check if JWT is expired                               │ │
│  │  ✅ Extract user info from JWT                            │ │
│  └────────────────────────────────────────────────────────────┘ │
└────────────────────────────────┬────────────────────────────────┘
                                 │ ✅ Valid JWT
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                3. Authorization (SecurityConfig)                 │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  ✅ Check if user has required role                       │ │
│  │     - /api/v1/parkings/my → requires OWNER role           │ │
│  │     - /api/v1/parkings → public                           │ │
│  │  ✅ Check if user owns the resource                       │ │
│  └────────────────────────────────────────────────────────────┘ │
└────────────────────────────────┬────────────────────────────────┘
                                 │ ✅ Authorized
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                    4. Controller Method                          │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  @PatchMapping("/my/availability")                         │ │
│  │  public ResponseEntity updateMyParkingAvailability(...)    │ │
│  └────────────────────────────────────────────────────────────┘ │
└────────────────────────────────┬────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   5. Input Validation (Jakarta)                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  @Valid UpdateAvailabilityRequest                          │ │
│  │  ✅ @NotNull availableSpots                                │ │
│  │  ✅ @PositiveOrZero availableSpots                         │ │
│  └────────────────────────────────────────────────────────────┘ │
└────────────────────────────────┬────────────────────────────────┘
                                 │ ✅ Valid input
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                    6. Business Logic (Service)                   │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  ✅ Check if parking exists                                │ │
│  │  ✅ Check if availableSpots <= capacity                    │ │
│  │  ✅ Update database                                        │ │
│  │  ✅ Broadcast WebSocket event                              │ │
│  └────────────────────────────────────────────────────────────┘ │
└────────────────────────────────┬────────────────────────────────┘
                                 │ ✅ Success
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                     7. Response + WebSocket                      │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  HTTP 200 OK → { parkingId: 1, availableSpots: 4 }        │ │
│  │  +                                                          │ │
│  │  WebSocket Broadcast → All connected clients               │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🎯 FLUJO DE BÚSQUEDA GEOESPACIAL

```
┌──────────────┐                    ┌──────────────┐
│   FRONTEND   │                    │   BACKEND    │
│   (Driver)   │                    │              │
└──────┬───────┘                    └──────┬───────┘
       │                                   │
       │  1. User opens map                │
       │  Request geolocation              │
       │  navigator.geolocation            │
       │  .getCurrentPosition()            │
       │                                   │
       │  2. GET /api/v1/parkings/nearby  │
       ├──────────────────────────────────>│
       │  ?lat=-34.6037                    │
       │  &lon=-58.3816                    │
       │  &radius=5                        │  3. Calculate distance
       │  &maxPrice=10                     │  using Haversine formula
       │  &minAvailability=2               │
       │                                   │  distance = 2 * R * asin(
       │                                   │    sqrt(
       │                                   │      sin²((lat2-lat1)/2) +
       │                                   │      cos(lat1)*cos(lat2)*
       │                                   │      sin²((lon2-lon1)/2)
       │                                   │    )
       │                                   │  )
       │                                   │
       │                                   │  4. Filter by criteria
       │                                   │  - distance <= radius
       │                                   │  - hourlyRate <= maxPrice
       │                                   │  - availableSpots >= minAvail
       │                                   │
       │                                   │  5. Sort by distance (ASC)
       │                                   │
       │                                   │  6. Paginate results
       │                                   │  limit=10, offset=0
       │                                   │
       │  7. 200 OK                        │
       │<──────────────────────────────────┤
       │  {                                │
       │    data: [                        │
       │      {                            │
       │        id: 1,                     │
       │        name: "Parking Central",   │
       │        latitude: -34.6037,        │
       │        longitude: -58.3816,       │
       │        availableSpots: 5,         │
       │        capacity: 10,              │
       │        hourlyRate: 8.50,          │
       │        distance: 0.5 // km        │
       │      },                           │
       │      ...                          │
       │    ],                             │
       │    pagination: {                  │
       │      total: 25,                   │
       │      limit: 10,                   │
       │      offset: 0,                   │
       │      hasMore: true                │
       │    }                              │
       │  }                                │
       │                                   │
       │  8. Render markers on map 🗺️     │
       │  parkings.forEach(p => {          │
       │    L.marker([p.lat, p.lon])       │
       │     .addTo(map);                  │
       │  });                              │
       │                                   │
```

---

**¡Documentación visual completa! 🎉**

