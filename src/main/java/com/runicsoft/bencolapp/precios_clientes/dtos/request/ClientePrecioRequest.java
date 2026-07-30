package com.runicsoft.bencolapp.precios_clientes.dtos.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientePrecioRequest {
    private Long clienteId;
    private Long productoId;
    private BigDecimal precio;
}
