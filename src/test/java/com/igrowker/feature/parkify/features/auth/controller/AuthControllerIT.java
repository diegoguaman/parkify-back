package com.igrowker.feature.parkify.features.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igrowker.feature.parkify.features.auth.dto.request.LoginRequest;
import com.igrowker.feature.parkify.features.auth.dto.request.RegisterRequest;
import com.igrowker.feature.parkify.features.auth.dto.response.LoginResponse;
import com.igrowker.feature.parkify.features.auth.repository.AuthUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties = {
        "jwt.secret=TXlUZXN0U2VjcmV0S2V5VGhhdElzTG9uZ0Vub3VnaDEyMw==",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AuthControllerIT {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15")
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthUserRepository authUserRepository;

    private LoginRequest loginRequestExistingUser;
    private final String existingUserEmail = "test.owner.it@example.com";
    private final String existingUserPassword = "password";

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        loginRequestExistingUser = new LoginRequest(existingUserEmail, existingUserPassword);
        authUserRepository.findByEmail("new.it.user@example.com").ifPresent(authUserRepository::delete);
        authUserRepository.findByEmail("new.driver.it@example.com").ifPresent(authUserRepository::delete);
    }

    @AfterEach
    void tearDown() {
        authUserRepository.findByEmail("new.it.user@example.com").ifPresent(authUserRepository::delete);
        authUserRepository.findByEmail("new.driver.it@example.com").ifPresent(authUserRepository::delete);
    }

    @Nested
    @DisplayName("Login Endpoint (/api/v1/auth/login)")
    @Sql(statements = {
            "DELETE FROM users WHERE email = 'test.owner.it@example.com';",
            "INSERT INTO users (username, email, password, role, contact_phone, created_at, updated_at) " +
                    "VALUES ('testuser', 'test.owner.it@example.com', " +
                    "'$2a$10$PK8hd/HO2R/mvqqhdyhS7.QjRF5AKbCMeclnVm.yV6tchnT/CkN6K', " +
                    "'OWNER', " +
                    "'1234567890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP );"
    })
    class LoginTests {
        @Test
        void login_ValidCredentials_ShouldReturnOkAndTokenAndEmail() throws Exception {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequestExistingUser)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.email", is(existingUserEmail)));
        }

        @Test
        void login_InvalidCredentials_ShouldReturnUnauthorized() throws Exception {
            final LoginRequest invalidLogin = new LoginRequest(existingUserEmail, "wrongpassword");
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidLogin)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void login_UserNotFound_ShouldReturnUnauthorized() throws Exception {
            final LoginRequest nonExistentUserLogin = new LoginRequest("nonexistent.user@example.com", existingUserPassword);
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nonExistentUserLogin)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void login_InvalidRequestBody_MissingEmail_ShouldReturnBadRequest() throws Exception {
            final LoginRequest invalidRequest = new LoginRequest("", existingUserPassword);
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void login_InvalidRequestBody_MissingPassword_ShouldReturnBadRequest() throws Exception {
            final LoginRequest invalidRequest = new LoginRequest(existingUserEmail, "");
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Register Endpoint (/api/v1/auth/register)")
    class RegisterTests {

        private final String newUserEmail = "new.it.user@example.com";
        private final String newUserPass = "password123";
        private final String newUserName = "New IT User";
        private final String newUserRole = "OWNER";
        private final String newUserPhone = "9876543210";

        @Test
        void register_ValidRequest_ShouldReturnCreatedAndUserData() throws Exception {
            RegisterRequest registerRequest = new RegisterRequest(
                    newUserName, newUserEmail, newUserPass, newUserRole, newUserPhone
            );

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(header().exists(HttpHeaders.LOCATION))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.username", is(newUserName)))
                    .andExpect(jsonPath("$.email", is(newUserEmail)))
                    .andExpect(jsonPath("$.role", is(newUserRole)))
                    .andExpect(jsonPath("$.contactPhone", is(newUserPhone)))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.updatedAt").isNotEmpty());
        }

        @Test
        void register_EmailAlreadyExists_ShouldReturnBadRequest() throws Exception {
            final RegisterRequest initialRegisterRequest = new RegisterRequest(
                    "UserToConflict", newUserEmail, newUserPass, "DRIVER", "111222333"
            );
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(initialRegisterRequest)))
                    .andExpect(status().isCreated());

            final RegisterRequest duplicateRegisterRequest = new RegisterRequest(
                    "AnotherUser", newUserEmail, "anotherPass", "OWNER", "444555666"
            );
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateRegisterRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is("This email already registered.")));
        }

        @Test
        void register_InvalidRole_ShouldReturnBadRequest() throws Exception {
            final RegisterRequest registerRequest = new RegisterRequest(
                    newUserName, "invalid.role.user@example.com", newUserPass, "INVALID_ROLE", newUserPhone
            );
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details.role", is("Role must be either DRIVER or OWNER")));
        }

        @Test
        void register_ShortPassword_ShouldReturnBadRequest() throws Exception {
            final RegisterRequest registerRequest = new RegisterRequest(
                    newUserName, "short.pass.user@example.com", "123", newUserRole, newUserPhone
            );

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details.password", is("Password must be at least 6 characters long")));
        }
    }

    @Nested
    @DisplayName("Get Current User Endpoint (/api/v1/auth/me)")
    @Sql(statements = {
            "DELETE FROM users WHERE email = 'test.owner.it@example.com';",
            "INSERT INTO users (id, username, email, password, role, contact_phone, created_at, updated_at) " +
                    "VALUES (101, 'testuserForMe', 'test.owner.it@example.com', " +
                    "'$2a$10$PK8hd/HO2R/mvqqhdyhS7.QjRF5AKbCMeclnVm.yV6tchnT/CkN6K', " +
                    "'OWNER', " +
                    "'1234567890', '2023-01-01T10:00:00', '2023-01-02T11:00:00' );"
    })
    class GetMeTests {

        private String authenticateAndGetToken(String email, String password) throws Exception {
            final MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
                    .andExpect(status().isOk())
                    .andReturn();
            final String responseString = result.getResponse().getContentAsString();
            final LoginResponse loginResponse = objectMapper.readValue(responseString, LoginResponse.class);
            return loginResponse.token();
        }

        @Test
        void getCurrentUser_Authenticated_ShouldReturnUserData() throws Exception {
            final String token = authenticateAndGetToken(existingUserEmail, existingUserPassword);

            mockMvc.perform(get("/api/v1/auth/me")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is("101")))
                    .andExpect(jsonPath("$.username", is("testuserForMe")))
                    .andExpect(jsonPath("$.email", is(existingUserEmail)))
                    .andExpect(jsonPath("$.role", is("OWNER")))
                    .andExpect(jsonPath("$.contactPhone", is("1234567890")))
                    .andExpect(jsonPath("$.createdAt", is("2023-01-01T10:00:00")))
                    .andExpect(jsonPath("$.updatedAt", is("2023-01-02T11:00:00")));
        }

        @Test
        void getCurrentUser_NotAuthenticated_ShouldReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/v1/auth/me")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }
    }
}