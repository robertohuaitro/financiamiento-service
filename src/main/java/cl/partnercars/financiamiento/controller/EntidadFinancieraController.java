package cl.partnercars.financiamiento.controller;

import cl.partnercars.financiamiento.model.EntidadFinanciera;
import cl.partnercars.financiamiento.service.EntidadFinancieraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/entidades-financieras")
@RequiredArgsConstructor
public class EntidadFinancieraController {

    private final EntidadFinancieraService entidadFinancieraService;

    @GetMapping
    public ResponseEntity<List<EntidadFinanciera>> listar() {
        log.debug("GET /entidades-financieras");
        return ResponseEntity.ok(entidadFinancieraService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntidadFinanciera> obtener(@PathVariable Long id) {
        log.debug("GET /entidades-financieras/{}", id);
        return ResponseEntity.ok(entidadFinancieraService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<EntidadFinanciera> crear(@Valid @RequestBody EntidadFinanciera entidad) {
        log.debug("POST /entidades-financieras con nombre {}", entidad.getNombre());
        EntidadFinanciera creada = entidadFinancieraService.crear(entidad);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getId())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntidadFinanciera> actualizar(@PathVariable Long id, @Valid @RequestBody EntidadFinanciera entidad) {
        log.debug("PUT /entidades-financieras/{}", id);
        return ResponseEntity.ok(entidadFinancieraService.actualizar(id, entidad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.debug("DELETE /entidades-financieras/{}", id);
        entidadFinancieraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
