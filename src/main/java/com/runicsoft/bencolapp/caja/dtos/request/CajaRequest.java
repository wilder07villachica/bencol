package com.runicsoft.bencolapp.caja.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CajaRequest {

    @NotNull(message = "El saldo inicial es obligatorio.")
    @DecimalMin(value = "0.00", message = "El saldo inicial no puede ser negativo.")
    private BigDecimal saldoInicial;
}