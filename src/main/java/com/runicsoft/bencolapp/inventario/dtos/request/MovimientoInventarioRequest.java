package com.runicsoft.bencolapp.inventario.dtos.request;

import com.runicsoft.bencolapp.inventario.utils.TipoMovimientoInventario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MovimientoInventarioRequest {

    @NotNull(message = "La referencia del producto es necesaria.")
    @Positive(message = "La referencia del producto debe ser mayor que cero.")
    private Long productoId;

    @NotNull(message = "El tipo de movimiento es obligatorio.")
    private TipoMovimientoInventario tipoMovimiento;

    @NotNull(message = "La cantidad es obligatoria.")
    @Positive(message = "La cantidad debe ser mayor que cero.")
    private Integer cantidad;

    @Size(max = 255, message = "La referencia no debe exceder los 255 caracteres.")
    private String referencia;
}