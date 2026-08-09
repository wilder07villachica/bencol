package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FlujoNetoPorMesResponse {
    private Integer anio;
    private Integer mes;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal flujoNeto;
}