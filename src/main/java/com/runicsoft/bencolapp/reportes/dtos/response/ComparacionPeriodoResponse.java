package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComparacionPeriodoResponse {
    private BigDecimal valorActual;
    private BigDecimal valorAnterior;
    private BigDecimal variacionAbsoluta;
    private BigDecimal variacionPorcentual;
}