package cl.partnercars.financiamiento.dto;

import cl.partnercars.financiamiento.model.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoSolicitud estado;
}
