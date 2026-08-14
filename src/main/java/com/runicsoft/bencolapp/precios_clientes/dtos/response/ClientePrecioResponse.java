package com.runicsoft.bencolapp.precios_clientes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClientePrecioResponse {

    private Long id;

    private Long clienteId;
    private String nombreCliente;

    private Long productoId;
    private String descripcionProducto;

    private BigDecimal precio;

    private LocalDateTime fechaCreacion;
}