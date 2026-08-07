package com.runicsoft.bencolapp.compras.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class CompraRequest {

    @NotNull(message = "La referencia del proveedor es necesaria.")
    @Positive(message = "La referencia del proveedor debe ser mayor que cero.")
    private Long proveedorId;

    @NotEmpty(message = "La compra debe contener al menos un producto.")
    @Valid
    private List<DetalleCompraRequest> detalles;
}