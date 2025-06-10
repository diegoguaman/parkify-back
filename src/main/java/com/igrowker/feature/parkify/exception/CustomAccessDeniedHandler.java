package com.igrowker.feature.parkify.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String json = String.format("""
            {
              "timestamp": "%s",
              "status": 403,
              "error": "Forbidden",
              "message": "No tenés permisos para acceder a este recurso. Requiere rol OWNER.",
              "path": "%s"
            }
            """, Instant.now(), request.getRequestURI());

        response.getWriter().write(json);
    }
}