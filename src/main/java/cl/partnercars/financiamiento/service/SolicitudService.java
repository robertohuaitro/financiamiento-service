package cl.partnercars.financiamiento.service;

import cl.partnercars.financiamiento.dto.SolicitudRequest;
import cl.partnercars.financiamiento.exception.ConflictoException;
import cl.partnercars.financiamiento.exception.RecursoNoEncontradoException;
import cl.partnercars.financiamiento.exception.SolicitudInvalidaException;
import cl.partnercars.financiamiento.model.EntidadFinanciera;
import cl.partnercars.financiamiento.model.EstadoSolicitud;
import cl.partnercars.financiamiento.model.SolicitudFinanciamiento;
import cl.partnercars.financiamiento.repository.SolicitudFinanciamientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final SolicitudFinanciamientoRepository solicitudRepository;
    private final EntidadFinancieraService entidadFinancieraService;

    @Transactional(readOnly = true)
    public List<SolicitudFinanciamiento> listar(Long entidadId, EstadoSolicitud estado) {
        if (entidadId != null && estado != null) {
            return solicitudRepository.findByEntidadFinancieraIdAndEstadoOrderByIdAsc(entidadId, estado);
        }
        if (entidadId != null) {
            return solicitudRepository.findByEntidadFinancieraIdOrderByIdAsc(entidadId);
        }
        if (estado != null) {
            return solicitudRepository.findByEstadoOrderByIdAsc(estado);
        }
        return solicitudRepository.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public SolicitudFinanciamiento obtener(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la solicitud con id " + id));
    }

    @Transactional
    public SolicitudFinanciamiento crear(SolicitudRequest request) {
        EntidadFinanciera entidad = validar(request);
        SolicitudFinanciamiento solicitud = SolicitudFinanciamiento.builder()
                .entidadFinanciera(entidad)
                .estado(EstadoSolicitud.PENDIENTE)
                .build();
        copiarDatos(request, solicitud);
        SolicitudFinanciamiento creada = solicitudRepository.save(solicitud);
        log.info("Solicitud {} creada para el vehículo {} con {} ({} cuotas)",
                creada.getId(), creada.getVehiculoId(), entidad.getNombre(), creada.getCuotas());
        return creada;
    }

    @Transactional
    public SolicitudFinanciamiento actualizar(Long id, SolicitudRequest request) {
        SolicitudFinanciamiento solicitud = obtener(id);
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ConflictoException("Solo se puede modificar una solicitud PENDIENTE; la solicitud " + id
                    + " está " + solicitud.getEstado());
        }
        solicitud.setEntidadFinanciera(validar(request));
        copiarDatos(request, solicitud);
        SolicitudFinanciamiento actualizada = solicitudRepository.save(solicitud);
        log.info("Solicitud {} actualizada", actualizada.getId());
        return actualizada;
    }

    @Transactional
    public SolicitudFinanciamiento cambiarEstado(Long id, EstadoSolicitud nuevoEstado) {
        SolicitudFinanciamiento solicitud = obtener(id);
        EstadoSolicitud estadoActual = solicitud.getEstado();
        if (!estadoActual.puedeCambiarA(nuevoEstado)) {
            throw new ConflictoException("La solicitud " + id + " no puede pasar de " + estadoActual + " a " + nuevoEstado);
        }
        solicitud.setEstado(nuevoEstado);
        SolicitudFinanciamiento actualizada = solicitudRepository.save(solicitud);
        log.info("Solicitud {} cambió de {} a {}", id, estadoActual, nuevoEstado);
        return actualizada;
    }

    @Transactional
    public void eliminar(Long id) {
        SolicitudFinanciamiento solicitud = obtener(id);
        solicitudRepository.delete(solicitud);
        log.info("Solicitud {} eliminada (estaba {})", id, solicitud.getEstado());
    }

    private EntidadFinanciera validar(SolicitudRequest request) {
        EntidadFinanciera entidad = entidadFinancieraService.obtener(request.getEntidadFinancieraId());
        if (request.getCuotas() > entidad.getPlazoMaximoMeses()) {
            throw new SolicitudInvalidaException(entidad.getNombre() + " permite como máximo "
                    + entidad.getPlazoMaximoMeses() + " cuotas");
        }
        return entidad;
    }

    private void copiarDatos(SolicitudRequest request, SolicitudFinanciamiento solicitud) {
        solicitud.setVehiculoId(request.getVehiculoId());
        solicitud.setRutCliente(request.getRutCliente().toUpperCase());
        solicitud.setNombreCliente(request.getNombreCliente());
        solicitud.setEmailCliente(request.getEmailCliente());
        solicitud.setMontoSolicitado(request.getMontoSolicitado());
        solicitud.setPie(request.getPie());
        solicitud.setCuotas(request.getCuotas());
    }
}
