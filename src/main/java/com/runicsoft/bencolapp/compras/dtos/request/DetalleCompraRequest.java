package com.runicsoft.bencolapp.compras.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleCompraRequest {

    @NotNull(message = "La referencia del producto es necesaria.")
    @Positive(message = "La referencia del producto debe ser mayor que cero.")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria.")
    @Positive(message = "La cantidad debe ser mayor que cero.")
    private Integer cantidad;

    @NotNull(message = "El costo unitario es obligatorio.")
    @DecimalMin(value = "0.01", message = "El costo unitario debe ser mayor que cero.")
    private BigDecimal costoUnitario;
}