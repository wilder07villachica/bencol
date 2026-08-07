package com.runicsoft.bencolapp.caja.dtos.response;

import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CajaResponse {
    private Long id;
    private BigDecimal saldoInicial;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal saldoActual;
    private BigDecimal saldoEsperado;
    private BigDecimal saldoReal;
    private BigDecimal diferencia;
    private EstadoCaja estado;
    private List<MovimientoCajaResponse> movimientos;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
}