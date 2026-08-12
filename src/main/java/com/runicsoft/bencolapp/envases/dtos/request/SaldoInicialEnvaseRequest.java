package com.runicsoft.bencolapp.envases.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaldoInicialEnvaseRequest {

    @NotNull(message = "El cliente es obligatorio.")
    private Long clienteId;

    @NotNull(message = "El producto es obligatorio.")
    private Long productoId;

    @NotNull(message = "La cantidad de envases propios es obligatoria.")
    @Min(value = 0, message = "La cantidad de envases propios no puede ser negativa.")
    private Integer cantidadPropios;

    @NotNull(message = "La cantidad de envases prestados es obligatoria.")
    @Min(value = 0, message = "La cantidad de envases prestados no puede ser negativa.")
    private Integer cantidadPrestados;

    private String referencia;
}