package com.runicsoft.bencolapp.caja.dtos.request;

import com.runicsoft.bencolapp.caja.utils.TipoMovimientoCaja;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovimientoCajaRequest {

    @NotNull(message = "La referencia de la caja es obligatoria.")
    private Long cajaId;

    @NotNull(message = "El tipo de movimiento es obligatorio.")
    private TipoMovimientoCaja tipoMovimiento;

    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero.")
    private BigDecimal monto;

    @NotBlank(message = "El concepto es obligatorio.")
    @Size(max = 255, message = "El concepto no debe superar los 255 caracteres.")
    private String concepto;

    @Size(max = 100, message = "La referencia no debe superar los 100 caracteres.")
    private String referencia;
}