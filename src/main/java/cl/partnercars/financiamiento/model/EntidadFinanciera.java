package cl.partnercars.financiamiento.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "entidades_financieras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EntidadFinanciera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entidad")
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "El nombre de la entidad es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @NotNull(message = "La tasa anual es obligatoria")
    @DecimalMin(value = "0.01", message = "La tasa anual debe ser mayor que cero")
    @DecimalMax(value = "100.00", message = "La tasa anual no puede superar el 100%")
    @Column(name = "tasa_anual", nullable = false, precision = 5, scale = 2)
    private BigDecimal tasaAnual;

    @NotNull(message = "El plazo máximo es obligatorio")
    @Min(value = 6, message = "El plazo máximo debe ser de al menos 6 meses")
    @Max(value = 96, message = "El plazo máximo no puede superar los 96 meses")
    @Column(name = "plazo_maximo_meses", nullable = false)
    private Integer plazoMaximoMeses;

    @JsonIgnore
    @OneToMany(mappedBy = "entidadFinanciera")
    private List<SolicitudFinanciamiento> solicitudes;
}
