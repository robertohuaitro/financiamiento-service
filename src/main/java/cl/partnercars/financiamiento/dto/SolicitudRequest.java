package cl.partnercars.financiamiento.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRequest {

    @NotNull(message = "La entidad financiera es obligatoria")
    private Long entidadFinancieraId;

    @NotNull(message = "El vehículo es obligatorio")
    @Positive(message = "El id del vehículo debe ser mayor que cero")
    private Long vehiculoId;

    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "^\\d{7,8}-[\\dkK]$", message = "El RUT debe tener el formato 12345678-9")
    private String rutCliente;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombreCliente;

    @NotBlank(message = "El email del cliente es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 120, message = "El email no puede superar los 120 caracteres")
    private String emailCliente;

    @NotNull(message = "El monto solicitado es obligatorio")
    @Positive(message = "El monto solicitado debe ser mayor que cero")
    private Long montoSolicitado;

    @NotNull(message = "El pie es obligatorio")
    @PositiveOrZero(message = "El pie no puede ser negativo")
    private Long pie;

    @NotNull(message = "Las cuotas son obligatorias")
    @Min(value = 6, message = "El crédito debe ser de al menos 6 cuotas")
    @Max(value = 96, message = "El crédito no puede superar las 96 cuotas")
    private Integer cuotas;
}
