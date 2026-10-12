package cl.partnercars.financiamiento.repository;

import cl.partnercars.financiamiento.model.EstadoSolicitud;
import cl.partnercars.financiamiento.model.SolicitudFinanciamiento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudFinanciamientoRepository extends JpaRepository<SolicitudFinanciamiento, Long> {

    @EntityGraph(attributePaths = "entidadFinanciera")
    List<SolicitudFinanciamiento> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = "entidadFinanciera")
    List<SolicitudFinanciamiento> findByEstadoOrderByIdAsc(EstadoSolicitud estado);

    @EntityGraph(attributePaths = "entidadFinanciera")
    List<SolicitudFinanciamiento> findByEntidadFinancieraIdOrderByIdAsc(Long entidadId);

    @EntityGraph(attributePaths = "entidadFinanciera")
    List<SolicitudFinanciamiento> findByEntidadFinancieraIdAndEstadoOrderByIdAsc(Long entidadId, EstadoSolicitud estado);

    boolean existsByEntidadFinancieraId(Long entidadId);
}
