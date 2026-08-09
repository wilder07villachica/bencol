package com.runicsoft.bencolapp.reportes.dtos.response;

import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResumenCajaResponse {
    private Long cajaId;
    private EstadoCaja estado;
    private BigDecimal saldoInicial;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal saldoActual;
    private BigDecimal ingresosPeriodo;
    private BigDecimal egresosPeriodo;
}