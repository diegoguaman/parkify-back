package com.igrowker.parking_management.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igrowker.common.config.SecurityBaseConfig;
import com.igrowker.common.exceptions.BadRequestException;
import com.igrowker.common.exceptions.ResourceNotFoundException;
import com.igrowker.parking_management.application.ParkingManagementService;
import com.igrowker.parking_management.infrastructure.dto.request.AvailabilityUpdateRequest;
import com.igrowker.parking_management.infrastructure.dto.response.AvailabilityUpdateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the {@link ParkingManagementController}.
 * Focuses on testing the web layer (request handling, validation, security checks, response generation)
 * by mocking the service layer. Uses {@link WebMvcTest}.
 */
@WebMvcTest(ParkingManagementController.class) // Enfoca la prueba solo en la capa del controlador web (API). Carga un contexto mínimo de Spring MVC.
@DisplayName("ParkingManagementController Tests")
@Import(SecurityBaseConfig.class) // Importamos la configuración de seguridad para que @PreAuthorize funcione en el controlador.
class ParkingManagementControllerTest {

    private static final String BASE_URL = "/parkings";
    private static final String TEST_PARKING_ID = "parking-123";
    // --- Explicación en Español ---
    // Este ID debe coincidir con el 'username' en @WithMockUser para las pruebas exitosas/autorizadas.
    private static final String TEST_OWNER_ID = "stub-owner-id";

    // --- Explicación en Español ---
    // Herramienta principal de Spring Test para simular peticiones HTTP al controlador sin iniciar un servidor real.
    @Autowired
    private MockMvc mockMvc;

    // --- Explicación en Español ---
    // Crea un 'mock' (simulacro) del ParkingManagementService usando Mockito y lo inyecta en el contexto de Spring.
    // Esto nos permite definir su comportamiento y verificar interacciones sin usar la implementación real del servicio.
    @MockBean
    private ParkingManagementService parkingManagementService;

    // --- Explicación en Español ---
    // Utilidad estándar de Jackson (incluida en Spring Boot Web) para convertir objetos Java (DTOs) a JSON y viceversa.
    // La necesitamos para crear el cuerpo (body) JSON de las peticiones simuladas.
    @Autowired
    private ObjectMapper objectMapper;

    // --- Explicación en Español ---
    // El contexto de la aplicación web cargado por Spring Boot Test.
    // Lo usamos en el setup para construir MockMvc de forma que integre la configuración de Spring Security.
    @Autowired
    private WebApplicationContext context;


    /**
     * Configures MockMvc before each test method runs.
     * Applies Spring Security filters to the MockMvc instance.
     */
    // --- Explicación en Español ---
    // Este método se ejecuta antes de CADA prueba (@Test) gracias a @BeforeEach.
    // Configura MockMvc para que las peticiones simuladas pasen por los filtros de Spring Security,
    // permitiendo así probar las anotaciones como @PreAuthorize.
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context) // Usa el contexto de la aplicación web
                .apply(springSecurity()) // Aplica la configuración de seguridad de Spring
                .build(); // Construye la instancia de MockMvc
    }

    // =====================================================================================
    // --- Success Scenario Tests ---
    // =====================================================================================

    /**
     * Test PATCH /parkings/{parkingId}/availability
     * Scenario: Valid request, authenticated user with 'OWNER' role.
     * Expected: 200 OK response with the updated availability DTO.
     */
    @Test
    // --- Explicación en Español ---
    // @WithMockUser simula un usuario autenticado en el contexto de seguridad de Spring para esta prueba.
    // 'username' será el valor retornado por authentication.getName() en el controlador.
    // 'roles' define los roles asignados (Spring añade el prefijo "ROLE_" automáticamente para hasRole).
    @WithMockUser(username = TEST_OWNER_ID, roles = {"OWNER"})
    @DisplayName("updateAvailability - Success (200 OK)")
    void updateAvailability_whenValidRequestAndUserIsOwner_shouldReturnOk() throws Exception {
        // --- Explicación en Español ---
        // Arrange (Preparación): Definimos los datos de entrada (ID, DTO de petición)
        // y la respuesta que ESPERAMOS que el mock del servicio devuelva.
        String parkingId = TEST_PARKING_ID;
        AvailabilityUpdateRequest requestDto = new AvailabilityUpdateRequest(10);
        AvailabilityUpdateResponse expectedResponseDto = new AvailabilityUpdateResponse(parkingId, 10);

        // --- Explicación en Español ---
        // Configuramos el comportamiento del mock (BDDMockito.given / Mockito.when):
        // Cuando se llame al método 'updateAvailability' en el mock 'parkingManagementService'
        // con el 'parkingId' exacto, CUALQUIER instancia de AvailabilityUpdateRequest, y el 'ownerId' exacto...
        given(parkingManagementService.updateAvailability(eq(parkingId), any(AvailabilityUpdateRequest.class), eq(TEST_OWNER_ID)))
                .willReturn(expectedResponseDto); // ...entonces debe devolver el 'expectedResponseDto' que preparamos.

        // --- Explicación en Español ---
        // Act (Acción): Simulamos la petición HTTP PATCH usando MockMvc.
        mockMvc.perform(patch(BASE_URL + "/{parkingId}/availability", parkingId) // Construye la petición PATCH a la URL correcta
                        .contentType(MediaType.APPLICATION_JSON) // Indica que el cuerpo de la petición es JSON
                        .content(objectMapper.writeValueAsString(requestDto)) // Serializa el DTO de petición a una cadena JSON para el cuerpo
                        .accept(MediaType.APPLICATION_JSON)) // Indica que esperamos una respuesta en formato JSON
                // --- Explicación en Español ---
                // .andDo(print()) es MUY útil para depurar. Imprime los detalles de la petición simulada
                // y la respuesta recibida (headers, status, body) en la consola.
                .andDo(print())
                // --- Explicación en Español ---
                // Assert (Verificación): Comprobamos diferentes aspectos de la respuesta HTTP.
                .andExpect(status().isOk()) // Esperamos que el código de estado HTTP sea 200 OK.
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Esperamos que el Content-Type de la respuesta sea application/json.
                // --- Explicación en Español ---
                // Usamos JsonPath para verificar campos específicos dentro del cuerpo JSON de la respuesta.
                // '$' representa la raíz del JSON.
                .andExpect(jsonPath("$.parkingId").value(parkingId)) // Verifica que el campo 'parkingId' tenga el valor esperado.
                .andExpect(jsonPath("$.availableSpots").value(10)); // Verifica que el campo 'currentAvailableSpots' tenga el valor actualizado.

        // --- Explicación en Español ---
        // Verify (Confirmación): Usamos Mockito.verify para asegurarnos de que el método
        // 'updateAvailability' del mock 'parkingManagementService' fue llamado exactamente una vez
        // con los argumentos que esperamos (usando eq() y any() como en el 'given').
        verify(parkingManagementService).updateAvailability(eq(parkingId), any(AvailabilityUpdateRequest.class), eq(TEST_OWNER_ID));
    }

    // =====================================================================================
    // --- Validation Error Tests (Controller/DTO Level) ---
    // =====================================================================================

    /**
     * Test PATCH /parkings/{parkingId}/availability
     * Scenario: Parking ID in path is blank.
     * Expected: 400 Bad Request response due to {@link jakarta.validation.constraints.NotBlank}.
     */
    @Test
    @WithMockUser(username = TEST_OWNER_ID, roles = {"OWNER"}) // User needs to be authenticated to pass security filter first
    @DisplayName("updateAvailability - Bad Request (400) - Blank Parking ID")
    void updateAvailability_whenParkingIdIsBlank_shouldReturnBadRequest() throws Exception {
        // Arrange
        AvailabilityUpdateRequest requestDto = new AvailabilityUpdateRequest(10);
        String blankParkingId = " "; // Invalid ID according to @NotBlank

        // Act & Assert
        mockMvc.perform(patch(BASE_URL + "/{parkingId}/availability", blankParkingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()); // Esperamos 400 Bad Request (validación de @NotBlank en @PathVariable)

        // Verify
        // --- Explicación en Español ---
        // El servicio NUNCA debe ser llamado si la validación de parámetros de la petición falla
        // a nivel de las anotaciones de validación de Spring (@NotBlank, @Valid).
        verify(parkingManagementService, never()).updateAvailability(any(), any(), any());
    }

    /**
     * Test PATCH /parkings/{parkingId}/availability
     * Scenario: Request body has negative 'availableSpots'.
     * Expected: 400 Bad Request response due to {@link jakarta.validation.constraints.Min}.
     */
    @Test
    @WithMockUser(username = TEST_OWNER_ID, roles = {"OWNER"}) // User needs to be authenticated
    @DisplayName("updateAvailability - Bad Request (400) - Negative Available Spots")
    void updateAvailability_whenAvailableSpotsIsNegative_shouldReturnBadRequest() throws Exception {
        // Arrange
        // --- Explicación en Español ---
        // Creamos un DTO de petición con un valor inválido según la anotación @Min(0) en el DTO.
        AvailabilityUpdateRequest requestDto = new AvailabilityUpdateRequest(-5);

        // Act & Assert
        mockMvc.perform(patch(BASE_URL + "/{parkingId}/availability", TEST_PARKING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)) // Cuerpo de petición inválido
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()); // Esperamos 400 Bad Request (validación de @Min en el DTO @Valid)

        // Verify
        // --- Explicación en Español ---
        // El servicio NUNCA debe ser llamado si la validación del DTO de petición falla.
        verify(parkingManagementService, never()).updateAvailability(any(), any(), any());
    }

    /**
     * Test PATCH /parkings/{parkingId}/availability
     * Scenario: Request body has null 'availableSpots'.
     * Expected: 400 Bad Request response due to {@link jakarta.validation.constraints.NotNull}.
     */
    @Test
    @WithMockUser(username = TEST_OWNER_ID, roles = {"OWNER"}) // User needs to be authenticated
    @DisplayName("updateAvailability - Bad Request (400) - Null Available Spots")
    void updateAvailability_whenAvailableSpotsIsNull_shouldReturnBadRequest() throws Exception {
        // Arrange
        // --- Explicación en Español ---
        // Creamos un JSON inválido manualmente porque el DTO usaría 'int' que no puede ser null.
        // Si 'availableSpots' fuera 'Integer', podríamos haber usado new AvailabilityUpdateRequest(null).
        // Esto simula un cuerpo JSON donde falta el campo obligatorio o es explícitamente nulo.
        String invalidJsonBody = "{}"; // Sending empty JSON, or: "{\"availableSpots\": null}"

        // Act & Assert
        mockMvc.perform(patch(BASE_URL + "/{parkingId}/availability", TEST_PARKING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonBody) // Cuerpo de petición inválido (falta campo NotNull)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()); // Esperamos 400 Bad Request (validación de @NotNull en el DTO @Valid)

        // Verify
        // --- Explicación en Español ---
        // El servicio NUNCA debe ser llamado si la validación del DTO de petición falla.
        verify(parkingManagementService, never()).updateAvailability(any(), any(), any());
    }


    // =====================================================================================
    // --- Security Scenario Tests ---
    // =====================================================================================

    /**
     * Test PATCH /parkings/{parkingId}/availability
     * Scenario: No authenticated user sends the request.
     * Expected: 403 Forbidden response (due to {@link PreAuthorize} rejecting anonymous user).
     * Note: Returns 403 because security rules allow the request initially (permitAll),
     * but method-level security (@PreAuthorize) denies access to the anonymous user.
     */
    @Test
    @DisplayName("updateAvailability - Forbidden (403) - No User (Blocked by @PreAuthorize)") // Updated name and expected status
    void updateAvailability_whenUserIsNotAuthenticated_shouldReturnForbidden() throws Exception { // Updated method name
        // Arrange
        AvailabilityUpdateRequest requestDto = new AvailabilityUpdateRequest(10);

        // Act & Assert
        // --- Explicación en Español ---
        // Realizamos la petición SIN @WithMockUser, simulando un usuario anónimo.
        mockMvc.perform(patch(BASE_URL + "/{parkingId}/availability", TEST_PARKING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // --- Explicación en Español ---
                // Esperamos 403 Forbidden. Aunque la regla general es permitAll, la anotación @PreAuthorize("hasRole('OWNER')")
                // en el método del controlador se evalúa para el usuario actual (que es anónimo).
                // Como el usuario anónimo no tiene el rol 'OWNER', Spring Security lanza AccessDeniedException,
                // que por defecto resulta en una respuesta 403 Forbidden.
                .andExpect(status().isForbidden()); // <-- CORRECTED: Expect 403 Forbidden

        // Verify
        // --- Explicación en Español ---
        // El servicio NUNCA debe ser llamado si la seguridad a nivel de método falla.
        verify(parkingManagementService, never()).updateAvailability(any(), any(), any());
    }

    /**
     * Test PATCH /parkings/{parkingId}/availability
     * Scenario: Authenticated user has the wrong role ('USER' instead of 'OWNER').
     * Expected: 403 Forbidden response due to {@link PreAuthorize}.
     */
    @Test
    // --- Explicación en Español ---
    // Simulamos un usuario autenticado ('other-user') pero con un rol ('USER') que NO cumple
    // el requisito de @PreAuthorize("hasRole('OWNER')").
    @WithMockUser(username = "other-user", roles = {"USER"})
    @DisplayName("updateAvailability - Forbidden (403) - Wrong Role")
    void updateAvailability_whenUserHasWrongRole_shouldReturnForbidden() throws Exception {
        // Arrange
        AvailabilityUpdateRequest requestDto = new AvailabilityUpdateRequest(10);

        // Act & Assert
        mockMvc.perform(patch(BASE_URL + "/{parkingId}/availability", TEST_PARKING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // --- Explicación en Español ---
                // Esperamos 403 Forbidden porque la comprobación hasRole('OWNER') falla para el rol 'USER'.
                .andExpect(status().isForbidden());

        // Verify
        // --- Explicación en Español ---
        // El servicio NUNCA debe ser llamado si la autorización por rol falla.
        verify(parkingManagementService, never()).updateAvailability(any(), any(), any());
    }


    // =====================================================================================
    // --- Service Layer Exception Handling Tests (Assuming GlobalExceptionHandler) ---
    // =====================================================================================

    /**
     * Test PATCH /parkings/{parkingId}/availability
     * Scenario: Service throws {@link ResourceNotFoundException} (parking not found).
     * Expected: 404 Not Found response (handled by GlobalExceptionHandler).
     */
    @Test
    @WithMockUser(username = TEST_OWNER_ID, roles = {"OWNER"}) // User is authorized
    @DisplayName("updateAvailability - Not Found (404) - Parking Not Found (Service Level)")
    void updateAvailability_whenParkingNotFound_shouldReturnNotFound() throws Exception {
        // Arrange
        AvailabilityUpdateRequest requestDto = new AvailabilityUpdateRequest(10);
        String nonExistentParkingId = "non-existent-id";

        // --- Explicación en Español ---
        // Configuramos el mock del servicio para que LANCE una ResourceNotFoundException
        // cuando se le llame con el ID inexistente.
        given(parkingManagementService.updateAvailability(eq(nonExistentParkingId), any(AvailabilityUpdateRequest.class), eq(TEST_OWNER_ID)))
                .willThrow(new ResourceNotFoundException("Parking not found with id: " + nonExistentParkingId));

        // Act & Assert
        mockMvc.perform(patch(BASE_URL + "/{parkingId}/availability", nonExistentParkingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // --- Explicación en Español ---
                // Esperamos 404 Not Found. Asumimos que tenemos un GlobalExceptionHandler
                // que captura ResourceNotFoundException y la mapea a un estado HTTP 404.
                .andExpect(status().isNotFound());

        // Verify
        // --- Explicación en Español ---
        // El servicio SÍ fue llamado (porque la excepción se origina DENTRO de él),
        // así que verificamos que la llamada ocurrió.
        verify(parkingManagementService).updateAvailability(eq(nonExistentParkingId), any(AvailabilityUpdateRequest.class), eq(TEST_OWNER_ID));
    }

    /**
     * Test PATCH /parkings/{parkingId}/availability
     * Scenario: Service throws {@link AccessDeniedException} (e.g., user doesn't own the parking).
     * Expected: 403 Forbidden response (handled by Spring Security's default handler or GlobalExceptionHandler).
     */
    @Test
    @WithMockUser(username = TEST_OWNER_ID, roles = {"OWNER"}) // User has the role, but service denies access
    @DisplayName("updateAvailability - Forbidden (403) - User Not Owner (Service Level Check)")
    void updateAvailability_whenServiceDeniesAccess_shouldReturnForbidden() throws Exception {
        // Arrange
        AvailabilityUpdateRequest requestDto = new AvailabilityUpdateRequest(10);

        // --- Explicación en Español ---
        // Configuramos el mock del servicio para que LANCE AccessDeniedException.
        // Esto simula una comprobación de lógica de negocio DENTRO del servicio (ej: verificar si el ownerId coincide
        // con el dueño real del parking obtenido de la base de datos).
        given(parkingManagementService.updateAvailability(eq(TEST_PARKING_ID), any(AvailabilityUpdateRequest.class), eq(TEST_OWNER_ID)))
                .willThrow(new AccessDeniedException("User is not the owner of this parking lot"));

        // Act & Assert
        mockMvc.perform(patch(BASE_URL + "/{parkingId}/availability", TEST_PARKING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // --- Explicación en Español ---
                // Esperamos 403 Forbidden. Spring Security maneja AccessDeniedException por defecto
                // y devuelve 403. Un GlobalExceptionHandler también podría manejarlo así.
                .andExpect(status().isForbidden());

        // Verify
        // --- Explicación en Español ---
        // El servicio SÍ fue llamado.
        verify(parkingManagementService).updateAvailability(eq(TEST_PARKING_ID), any(AvailabilityUpdateRequest.class), eq(TEST_OWNER_ID));
    }


    /**
     * Test PATCH /parkings/{parkingId}/availability
     * Scenario: Service throws {@link BadRequestException} (e.g., business rule violation like available > total).
     * Expected: 400 Bad Request response (handled by GlobalExceptionHandler).
     */
    @Test
    @WithMockUser(username = TEST_OWNER_ID, roles = {"OWNER"}) // User is authorized
    @DisplayName("updateAvailability - Bad Request (400) - Business Rule Violation (Service Level)")
    void updateAvailability_whenServiceThrowsBadRequest_shouldReturnBadRequest() throws Exception {
        // Arrange
        // --- Explicación en Español ---
        // Usamos un valor que podría violar una regla de negocio (ej: más plazas disponibles que el total).
        AvailabilityUpdateRequest requestDto = new AvailabilityUpdateRequest(1000);

        // --- Explicación en Español ---
        // Configuramos el mock del servicio para que LANCE BadRequestException, simulando
        // el fallo de una validación de lógica de negocio dentro del servicio.
        given(parkingManagementService.updateAvailability(eq(TEST_PARKING_ID), any(AvailabilityUpdateRequest.class), eq(TEST_OWNER_ID)))
                .willThrow(new BadRequestException("Available spots cannot exceed total spots"));

        // Act & Assert
        mockMvc.perform(patch(BASE_URL + "/{parkingId}/availability", TEST_PARKING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                // --- Explicación en Español ---
                // Esperamos 400 Bad Request. Asumimos que tenemos un GlobalExceptionHandler
                // que captura BadRequestException y la mapea a un estado HTTP 400.
                .andExpect(status().isBadRequest());

        // Verify
        // --- Explicación en Español ---
        // El servicio SÍ fue llamado.
        verify(parkingManagementService).updateAvailability(eq(TEST_PARKING_ID), any(AvailabilityUpdateRequest.class), eq(TEST_OWNER_ID));
    }
}