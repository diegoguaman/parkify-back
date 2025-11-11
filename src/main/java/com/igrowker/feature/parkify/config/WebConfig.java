package com.igrowker.feature.parkify.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS para peticiones HTTP.
 * 
 * CORS (Cross-Origin Resource Sharing) permite que el frontend
 * (http://localhost:5173) se comunique con el backend (http://localhost:8080).
 * 
 * Nota: La configuración de CORS para WebSocket está en WebSocketConfig.java
 * 
 * @author Parkify Team
 * @version 1.0
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configura CORS para todas las peticiones HTTP.
     * 
     * En desarrollo: permite localhost:5173 (Vite dev server)
     * En producción: usa la variable de entorno FRONTEND_URL
     * 
     * @return Configurador de CORS
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Leer URL del frontend desde variable de entorno
                String frontendUrl = System.getenv("FRONTEND_URL");
                
                // Valores por defecto para desarrollo
                String[] allowedOrigins;
                
                if (frontendUrl != null && !frontendUrl.isBlank()) {
                    // Producción: usar la URL configurada + localhost para desarrollo
                    allowedOrigins = new String[]{
                        frontendUrl,
                        "http://localhost:5173",
                        "http://localhost:8080"
                    };
                    log.info("🌐 CORS configured for production: {}", frontendUrl);
                } else {
                    // Desarrollo: permitir localhost en varios puertos
                    allowedOrigins = new String[]{
                        "http://localhost:5173",  // Vite dev server (frontend)
                        "http://localhost:8080",  // Backend (para Swagger UI)
                        "http://localhost:3000",  // Alternativa común
                        "http://127.0.0.1:5173",
                        "http://127.0.0.1:8080"
                    };
                    log.info("🌐 CORS configured for development: localhost:5173");
                }

                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true) // Importante para cookies y autenticación
                        .maxAge(3600); // Cache preflight requests por 1 hora
            }
        };
    }
}