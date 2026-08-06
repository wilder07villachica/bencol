package com.runicsoft.bencolapp.inventario.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class InventarioRequest {

    @NotNull(message = "La referencia del producto es necesaria.")
    @Positive(message = "La referencia del producto debe ser mayor que cero.")
    private Long productoId;

    @NotNull(message = "El stock actual es obligatorio.")
    @PositiveOrZero(message = "El stock actual no puede ser negativo.")
    private Integer stockActual;

    @NotNull(message = "El stock mínimo es obligatorio.")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo.")
    private Integer stockMinimo;

    @PositiveOrZero(message = "El stock máximo no puede ser negativo.")
    private Integer stockMaximo;
}