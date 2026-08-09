package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EgresoPorMesResponse {
    private Integer anio;
    private Integer mes;
    private BigDecimal totalEgresos;
}