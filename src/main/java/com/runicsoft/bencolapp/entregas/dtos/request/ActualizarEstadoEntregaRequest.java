package com.runicsoft.bencolapp.entregas.dtos.request;

import com.runicsoft.bencolapp.entregas.utils.EstadoEntrega;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActualizarEstadoEntregaRequest {

    @NotNull(message = "El estado de la entrega es obligatorio.")
    private EstadoEntrega estado;

    @Size(
            max = 1000,
            message = "Las observaciones no deben superar los 1000 caracteres."
    )
    private String observaciones;

    private LocalDateTime fechaProgramada;

    @NotNull(message = "La versión de la entrega es obligatoria.")
    @PositiveOrZero(message = "La versión no puede ser negativa.")
    private Long version;
}
