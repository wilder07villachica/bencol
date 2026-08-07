package com.runicsoft.bencolapp.finanzas.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CuentaCobrarRequest {

    @NotNull(message = "La referencia de la venta es necesaria.")
    @Positive(message = "La referencia de la venta debe ser mayor que cero.")
    private Long ventaId;
}