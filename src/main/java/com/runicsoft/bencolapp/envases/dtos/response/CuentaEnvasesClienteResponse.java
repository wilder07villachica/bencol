package com.runicsoft.bencolapp.envases.dtos.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CuentaEnvasesClienteResponse {
    private Long id;
    private Long clienteId;
    private String nombreCliente;
    private Long productoId;
    private String codigoProducto;
    private String descripcionProducto;
    private Integer cantidadPropios;
    private Integer cantidadPrestados;
    private Integer cantidadTotal;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}