package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

@Data
public class ProductoStockBajoResponse {
    private Long productoId;
    private String codigoProducto;
    private String descripcionProducto;
    private Integer stockActual;
    private Integer stockMinimo;
    private Integer diferenciaStock;
}