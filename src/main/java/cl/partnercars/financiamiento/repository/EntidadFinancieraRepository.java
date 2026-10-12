package cl.partnercars.financiamiento.repository;

import cl.partnercars.financiamiento.model.EntidadFinanciera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntidadFinancieraRepository extends JpaRepository<EntidadFinanciera, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
