package com.runicsoft.bencolapp.caja.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CierreCajaRequest {

    @NotNull(message = "El saldo real contado es obligatorio.")
    @DecimalMin(value = "0.00", message = "El saldo real no puede ser negativo.")
    private BigDecimal saldoReal;
}