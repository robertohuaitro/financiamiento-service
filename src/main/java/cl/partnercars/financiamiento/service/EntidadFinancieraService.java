package cl.partnercars.financiamiento.service;

import cl.partnercars.financiamiento.exception.ConflictoException;
import cl.partnercars.financiamiento.exception.RecursoNoEncontradoException;
import cl.partnercars.financiamiento.model.EntidadFinanciera;
import cl.partnercars.financiamiento.repository.EntidadFinancieraRepository;
import cl.partnercars.financiamiento.repository.SolicitudFinanciamientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class EntidadFinancieraService {

    private final EntidadFinancieraRepository entidadFinancieraRepository;
    private final SolicitudFinanciamientoRepository solicitudRepository;

    @Transactional(readOnly = true)
    public List<EntidadFinanciera> listar() {
        return entidadFinancieraRepository.findAll(Sort.by("tasaAnual"));
    }

    @Transactional(readOnly = true)
    public EntidadFinanciera obtener(Long id) {
        return entidadFinancieraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la entidad financiera con id " + id));
    }

    @Transactional
    public EntidadFinanciera crear(EntidadFinanciera entidad) {
        if (entidadFinancieraRepository.existsByNombreIgnoreCase(entidad.getNombre())) {
            throw new ConflictoException("Ya existe la entidad financiera " + entidad.getNombre());
        }
        entidad.setId(null);
        EntidadFinanciera creada = entidadFinancieraRepository.save(entidad);
        log.info("Entidad financiera creada: {} con tasa {}% (id {})", creada.getNombre(), creada.getTasaAnual(), creada.getId());
        return creada;
    }

    @Transactional
    public EntidadFinanciera actualizar(Long id, EntidadFinanciera datos) {
        EntidadFinanciera entidad = obtener(id);
        if (entidadFinancieraRepository.existsByNombreIgnoreCaseAndIdNot(datos.getNombre(), id)) {
            throw new ConflictoException("Ya existe otra entidad financiera con el nombre " + datos.getNombre());
        }
        entidad.setNombre(datos.getNombre());
        entidad.setTasaAnual(datos.getTasaAnual());
        entidad.setPlazoMaximoMeses(datos.getPlazoMaximoMeses());
        EntidadFinanciera actualizada = entidadFinancieraRepository.save(entidad);
        log.info("Entidad financiera actualizada: {} (id {})", actualizada.getNombre(), actualizada.getId());
        return actualizada;
    }

    @Transactional
    public void eliminar(Long id) {
        EntidadFinanciera entidad = obtener(id);
        if (solicitudRepository.existsByEntidadFinancieraId(id)) {
            throw new ConflictoException("No se puede eliminar " + entidad.getNombre() + " porque tiene solicitudes asociadas");
        }
        entidadFinancieraRepository.delete(entidad);
        log.info("Entidad financiera eliminada: {} (id {})", entidad.getNombre(), id);
    }
}
