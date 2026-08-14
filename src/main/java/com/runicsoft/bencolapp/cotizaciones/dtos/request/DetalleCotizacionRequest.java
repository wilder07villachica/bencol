package com.runicsoft.bencolapp.cotizaciones.dtos.request;

import com.runicsoft.bencolapp.envases.utils.TipoMovimientoEnvase;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleCotizacionRequest {

    @NotNull(message = "La referencia del producto es necesaria.")
    @Positive(message = "La referencia del producto debe ser mayor que cero.")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria.")
    @Positive(message = "La cantidad debe ser mayor que cero.")
    private Integer cantidad;

    private TipoMovimientoEnvase modalidadEnvase;

    @DecimalMin(value = "0.01", message = "El precio manual debe ser mayor que cero.")
    private BigDecimal precioManual;
}