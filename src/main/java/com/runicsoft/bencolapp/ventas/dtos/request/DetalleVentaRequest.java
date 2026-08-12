package com.runicsoft.bencolapp.ventas.dtos.request;

import com.runicsoft.bencolapp.envases.utils.TipoMovimientoEnvase;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleVentaRequest {

    @NotNull(message = "El producto es obligatorio.")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad debe ser mayor a cero.")
    private Integer cantidad;

    private Integer envasesDevueltos;

    private TipoMovimientoEnvase modalidadEnvase;

    @DecimalMin(value = "0.01", message = "El precio manual debe ser mayor a cero.")
    private BigDecimal precioManual;
}