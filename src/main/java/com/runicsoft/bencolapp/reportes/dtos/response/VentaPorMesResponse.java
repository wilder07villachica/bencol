package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VentaPorMesResponse {
    private Integer anio;
    private Integer mes;
    private Long cantidadVentas;
    private BigDecimal totalVendido;
}