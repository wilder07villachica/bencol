package com.runicsoft.bencolapp.cotizaciones.dtos.request;

import com.runicsoft.bencolapp.cotizaciones.utils.TipoPrecioCotizacion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CotizacionRequest {

    @NotNull(message = "La referencia del cliente es necesaria.")
    @Positive(message = "La referencia del cliente debe ser mayor que cero.")
    private Long clienteId;

    @NotNull(message = "El porcentaje de impuesto es obligatorio.")
    @DecimalMin(value = "0.00", message = "El porcentaje de impuesto no puede ser negativo.")
    private BigDecimal porcentajeImpuesto;

    private LocalDate fechaVencimiento;

    @Size(max = 500, message = "Las condiciones de pago no deben superar los 500 caracteres.")
    private String condicionesPago;

    @Size(max = 255, message = "El plazo de entrega no debe superar los 255 caracteres.")
    private String plazoEntrega;

    @Size(max = 1000, message = "La observación no debe superar los 1000 caracteres.")
    private String observacion;

    @NotEmpty(message = "La cotización debe contener al menos un producto.")
    @Valid
    private List<DetalleCotizacionRequest> detalles;

    @NotNull(message = "Debe indicar si los precios incluyen IGV.")
    private TipoPrecioCotizacion tipoPrecio;
}