package com.runicsoft.bencolapp.finanzas.dtos.request;

import com.runicsoft.bencolapp.finanzas.utils.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoProveedorRequest {

    @NotNull(message = "La referencia de la cuenta por pagar es necesaria.")
    @Positive(message = "La referencia de la cuenta debe ser mayor que cero.")
    private Long cuentaPagarId;

    @NotNull(message = "El monto del pago es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero.")
    private BigDecimal monto;

    @NotNull(message = "El método de pago es obligatorio.")
    private MetodoPago metodoPago;

    @Size(max = 100, message = "La referencia no debe exceder los 100 caracteres.")
    private String referencia;
}