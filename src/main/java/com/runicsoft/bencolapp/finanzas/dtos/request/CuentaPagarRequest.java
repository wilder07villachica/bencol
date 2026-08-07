package com.runicsoft.bencolapp.finanzas.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CuentaPagarRequest {

    @NotNull(message = "La referencia de la compra es necesaria.")
    @Positive(message = "La referencia de la compra debe ser mayor que cero.")
    private Long compraId;
}