package com.runicsoft.bencolapp.finanzas.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResumenFinancieroResponse {
    private BigDecimal totalVendido;
    private BigDecimal totalCobrado;
    private BigDecimal totalPorCobrar;
    private Integer cantidadCuentas;
    private Integer cantidadPendientes;
    private Integer cantidadParciales;
    private Integer cantidadPagadas;
    private Integer cantidadAnuladas;
}