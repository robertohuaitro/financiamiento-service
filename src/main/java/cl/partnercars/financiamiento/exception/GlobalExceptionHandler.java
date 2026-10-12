package cl.partnercars.financiamiento.exception;

import cl.partnercars.financiamiento.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException e, HttpServletRequest request) {
        log.warn("Recurso no encontrado en {}: {}", request.getRequestURI(), e.getMessage());
        return construir(HttpStatus.NOT_FOUND, e.getMessage(), request, null, null);
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponse> manejarConflicto(ConflictoException e, HttpServletRequest request) {
        log.warn("Conflicto en {}: {}", request.getRequestURI(), e.getMessage());
        return construir(HttpStatus.CONFLICT, e.getMessage(), request, null, null);
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorResponse> manejarSolicitudInvalida(SolicitudInvalidaException e, HttpServletRequest request) {
        log.warn("Solicitud inválida en {}: {}", request.getRequestURI(), e.getMessage());
        return construir(HttpStatus.BAD_REQUEST, e.getMessage(), request, null, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> detalles = new LinkedHashMap<>();
        for (FieldError campo : e.getBindingResult().getFieldErrors()) {
            detalles.putIfAbsent(campo.getField(), campo.getDefaultMessage());
        }
        log.warn("Validación fallida en {}: {}", request.getRequestURI(), detalles);
        return construir(HttpStatus.BAD_REQUEST, "La solicitud tiene campos inválidos", request, detalles, null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarCuerpoIlegible(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("Cuerpo ilegible en {}: {}", request.getRequestURI(), e.getMessage());
        return construir(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es un JSON válido o tiene un tipo de dato incorrecto", request, null, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoInvalido(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String mensaje = "El parámetro '" + e.getName() + "' tiene un valor inválido: " + e.getValue();
        log.warn("Parámetro inválido en {}: {}", request.getRequestURI(), mensaje);
        return construir(HttpStatus.BAD_REQUEST, mensaje, request, null, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> manejarMetodoNoPermitido(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String mensaje = "El método " + e.getMethod() + " no está permitido en esta ruta";
        return construir(HttpStatus.METHOD_NOT_ALLOWED, mensaje, request, null, e.getHeaders());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> manejarTipoContenido(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        return construir(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "El Content-Type debe ser application/json", request, null, e.getHeaders());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> manejarRutaInexistente(NoResourceFoundException e, HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, "La ruta solicitada no existe", request, null, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarIntegridad(DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("Violación de integridad en {}: {}", request.getRequestURI(), e.getMostSpecificCause().getMessage());
        return construir(HttpStatus.CONFLICT, "La operación viola una restricción de integridad de los datos", request, null, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarInesperado(Exception e, HttpServletRequest request) {
        log.error("Error inesperado en {}", request.getRequestURI(), e);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor", request, null, null);
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus estado, String mensaje, HttpServletRequest request,
                                                    Map<String, String> detalles, HttpHeaders cabeceras) {
        ErrorResponse cuerpo = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(estado.value())
                .error(estado.getReasonPhrase())
                .mensaje(mensaje)
                .ruta(request.getRequestURI())
                .detalles(detalles)
                .build();
        ResponseEntity.BodyBuilder respuesta = ResponseEntity.status(estado);
        if (cabeceras != null) {
            respuesta.headers(cabeceras);
        }
        return respuesta.body(cuerpo);
    }
}
