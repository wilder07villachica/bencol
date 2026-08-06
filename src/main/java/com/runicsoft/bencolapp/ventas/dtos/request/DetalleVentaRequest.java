package com.runicsoft.bencolapp.ventas.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DetalleVentaRequest {

    @NotNull(message = "La referencia del producto es necesaria.")
    @Positive(message = "La referencia del producto debe ser mayor que cero.")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria.")
    @Positive(message = "La cantidad debe ser mayor que cero.")
    private Integer cantidad;
}