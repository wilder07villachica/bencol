package com.runicsoft.bencolapp.entregas.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AsignarEntregaRequest {

    @NotNull(message = "El responsable de la entrega es obligatorio.")
    @Positive(message = "El identificador del responsable debe ser mayor que cero.")
    private Long repartidorId;

    @NotNull(message = "La versión de la entrega es obligatoria.")
    @PositiveOrZero(message = "La versión no puede ser negativa.")
    private Long version;
}
