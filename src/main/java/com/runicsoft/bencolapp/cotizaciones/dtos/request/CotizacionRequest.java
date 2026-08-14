package com.runicsoft.bencolapp.cotizaciones.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CotizacionRequest {

    @NotNull(message = "La referencia de la empresa es necesaria.")
    @Positive(message = "La referencia de la empresa debe ser mayor que cero.")
    private Long empresaId;

    @NotNull(message = "La referencia del cliente es necesaria.")
    @Positive(message = "La referencia del cliente debe ser mayor que cero.")
    private Long clienteId;

    private LocalDate fechaVencimiento;

    @Size(max = 500, message = "La observación no debe superar los 500 caracteres.")
    private String observacion;

    @NotEmpty(message = "La cotización debe contener al menos un producto.")
    @Valid
    private List<DetalleCotizacionRequest> detalles;
}