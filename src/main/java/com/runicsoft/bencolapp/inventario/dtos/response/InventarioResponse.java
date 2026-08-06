package com.runicsoft.bencolapp.inventario.dtos.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventarioResponse {
    private Long id;
    private Long productoId;
    private String codigoProducto;
    private String descripcionProducto;
    private Integer stockActual;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}