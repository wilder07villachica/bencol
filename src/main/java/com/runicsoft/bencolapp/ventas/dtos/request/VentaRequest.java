package com.runicsoft.bencolapp.ventas.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class VentaRequest {

    @NotNull(message = "La referencia del cliente es necesaria.")
    @Positive(message = "La referencia del cliente debe ser mayor que cero.")
    private Long clienteId;

    @NotEmpty(message = "La venta debe contener al menos un producto.")
    @Valid
    private List<DetalleVentaRequest> detalles;
}