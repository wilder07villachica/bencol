package com.runicsoft.bencolapp.entregas.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ObservacionEntregaRequest {

    @NotBlank(message = "La observación es obligatoria.")
    @Size(
            max = 1000,
            message = "La observación no debe superar los 1000 caracteres."
    )
    private String observaciones;

    @NotNull(message = "La versión de la entrega es obligatoria.")
    @PositiveOrZero(message = "La versión no puede ser negativa.")
    private Long version;
}
