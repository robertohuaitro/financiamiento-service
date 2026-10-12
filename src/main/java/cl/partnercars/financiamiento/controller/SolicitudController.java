package cl.partnercars.financiamiento.controller;

import cl.partnercars.financiamiento.dto.CambioEstadoRequest;
import cl.partnercars.financiamiento.dto.SolicitudRequest;
import cl.partnercars.financiamiento.model.EstadoSolicitud;
import cl.partnercars.financiamiento.model.SolicitudFinanciamiento;
import cl.partnercars.financiamiento.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @GetMapping
    public ResponseEntity<List<SolicitudFinanciamiento>> listar(@RequestParam(required = false) Long entidadId,
                                                                @RequestParam(required = false) EstadoSolicitud estado) {
        log.debug("GET /solicitudes con entidadId={} y estado={}", entidadId, estado);
        return ResponseEntity.ok(solicitudService.listar(entidadId, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudFinanciamiento> obtener(@PathVariable Long id) {
        log.debug("GET /solicitudes/{}", id);
        return ResponseEntity.ok(solicitudService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<SolicitudFinanciamiento> crear(@Valid @RequestBody SolicitudRequest request) {
        log.debug("POST /solicitudes para el vehículo {}", request.getVehiculoId());
        SolicitudFinanciamiento creada = solicitudService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getId())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitudFinanciamiento> actualizar(@PathVariable Long id, @Valid @RequestBody SolicitudRequest request) {
        log.debug("PUT /solicitudes/{}", id);
        return ResponseEntity.ok(solicitudService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudFinanciamiento> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
        log.debug("PATCH /solicitudes/{}/estado a {}", id, request.getEstado());
        return ResponseEntity.ok(solicitudService.cambiarEstado(id, request.getEstado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.debug("DELETE /solicitudes/{}", id);
        solicitudService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
