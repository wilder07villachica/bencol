package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResumenFinanzasResponse {
    private BigDecimal totalCobrado;
    private BigDecimal totalPorCobrar;
    private BigDecimal totalPagadoProveedores;
    private BigDecimal totalPorPagar;
}