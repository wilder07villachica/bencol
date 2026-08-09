package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VentaPorDiaResponse {
    private LocalDate fecha;
    private Long cantidadVentas;
    private BigDecimal totalVendido;
}