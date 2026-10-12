package cl.partnercars.financiamiento.security;

import cl.partnercars.financiamiento.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Log4j2
@Component
@RequiredArgsConstructor
public class ManejadorErroresSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        Object errorToken = request.getAttribute(JwtAuthorizationFilter.ATRIBUTO_ERROR_TOKEN);
        String mensaje = errorToken != null
                ? errorToken.toString()
                : "Debes enviar un token válido en la cabecera Authorization: Bearer <token>";
        log.warn("Acceso sin autenticar a {} {}", request.getMethod(), request.getRequestURI());
        escribir(request, response, HttpStatus.UNAUTHORIZED, mensaje);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
            throws IOException {
        log.warn("Acceso denegado a {} {}", request.getMethod(), request.getRequestURI());
        escribir(request, response, HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta operación");
    }

    private void escribir(HttpServletRequest request, HttpServletResponse response, HttpStatus estado, String mensaje)
            throws IOException {
        ErrorResponse cuerpo = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(estado.value())
                .error(estado.getReasonPhrase())
                .mensaje(mensaje)
                .ruta(request.getRequestURI())
                .build();
        response.setStatus(estado.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), cuerpo);
    }
}
