package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResumenVentasResponse {
    private BigDecimal totalVendido;
    private Long cantidadVentas;
    private BigDecimal ticketPromedio;
}