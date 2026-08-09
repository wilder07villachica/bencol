package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoMasVendidoResponse {
    private Long productoId;
    private String codigoProducto;
    private String descripcionProducto;
    private Long cantidadVendida;
    private Long unidadesFisicasVendidas;
    private BigDecimal totalVendido;
}