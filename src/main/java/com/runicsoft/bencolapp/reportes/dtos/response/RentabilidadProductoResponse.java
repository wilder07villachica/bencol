package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RentabilidadProductoResponse {
    private Long productoId;
    private String codigoProducto;
    private String descripcionProducto;
    private Long cantidadVendida;
    private Long unidadesFisicasVendidas;
    private BigDecimal totalVentas;
    private BigDecimal costoPromedioUnitario;
    private BigDecimal costoEstimado;
    private BigDecimal margenEstimado;
    private BigDecimal margenPorcentual;
}