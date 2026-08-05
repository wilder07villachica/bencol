package com.runicsoft.bencolapp.precios_clientes.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientePrecioRequest {

    @NotNull(message = "La referencia del cliente es necesaria.")
    @Positive(message = "La referencia del cliente debe ser mayor que cero.")
    private Long clienteId;

    @NotNull(message = "La referencia del producto es necesaria.")
    @Positive(message = "La referencia del producto debe ser mayor que cero.")
    private Long productoId;

    @NotNull(message = "El precio es obligatorio para este registro.")
    @Positive(message = "EL precio debe ser mayor a 0.")
    private BigDecimal precio;
}
