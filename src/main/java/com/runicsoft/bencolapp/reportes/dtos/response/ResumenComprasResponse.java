package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResumenComprasResponse {
    private BigDecimal totalComprado;
    private Long cantidadCompras;
}