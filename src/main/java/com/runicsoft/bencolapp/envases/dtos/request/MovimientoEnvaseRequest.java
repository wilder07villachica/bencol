package com.runicsoft.bencolapp.envases.dtos.request;

import com.runicsoft.bencolapp.envases.utils.TipoMovimientoEnvase;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MovimientoEnvaseRequest {

    @NotNull(message = "El cliente es obligatorio.")
    private Long clienteId;

    @NotNull(message = "El producto es obligatorio.")
    private Long productoId;

    @NotNull(message = "El tipo de movimiento es obligatorio.")
    private TipoMovimientoEnvase tipoMovimiento;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad debe ser mayor a cero.")
    private Integer cantidad;

    @Size(max = 255, message = "La referencia no puede superar los 255 caracteres.")
    private String referencia;
}