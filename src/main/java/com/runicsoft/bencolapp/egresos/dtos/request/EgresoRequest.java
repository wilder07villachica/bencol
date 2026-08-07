package com.runicsoft.bencolapp.egresos.dtos.request;

import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EgresoRequest {

    @NotNull(message = "La categoría del egreso es obligatoria.")
    private CategoriaEgreso categoria;

    @NotBlank(message = "El concepto es obligatorio.")
    @Size(max = 255, message = "El concepto no debe superar los 255 caracteres.")
    private String concepto;

    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero.")
    private BigDecimal monto;

    @Size(max = 100, message = "La referencia no debe superar los 100 caracteres.")
    private String referencia;
}