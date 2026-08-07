package com.runicsoft.bencolapp.compras.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleCompraResponse {
    private Long id;
    private Long productoId;
    private String codigoProducto;
    private String descripcionProducto;
    private Integer cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal subtotal;
}