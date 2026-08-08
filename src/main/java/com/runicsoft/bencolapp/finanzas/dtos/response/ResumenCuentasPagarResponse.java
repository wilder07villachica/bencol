package com.runicsoft.bencolapp.finanzas.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResumenCuentasPagarResponse {
    private BigDecimal totalComprado;
    private BigDecimal totalPagado;
    private BigDecimal totalPorPagar;
    private Integer cantidadCuentas;
    private Integer cantidadPendientes;
    private Integer cantidadParciales;
    private Integer cantidadPagadas;
    private Integer cantidadAnuladas;
}