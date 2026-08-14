package com.runicsoft.bencolapp.cotizaciones.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "El precio unitario es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor que cero.")
    private BigDecimal precioUnitario;

    @Size(max = 255, message = "La frecuencia de abastecimiento no debe superar los 255 caracteres.")
    private String frecuenciaAbastecimiento;

    @Size(max = 500, message = "La descripción adicional no debe superar los 500 caracteres.")
    private String descripcionAdicional;
}