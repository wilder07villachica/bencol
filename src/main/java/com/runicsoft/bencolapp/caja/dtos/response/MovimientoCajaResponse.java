package com.runicsoft.bencolapp.caja.dtos.response;

import com.runicsoft.bencolapp.caja.utils.TipoMovimientoCaja;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MovimientoCajaResponse {
    private Long id;
    private Long cajaId;
    private TipoMovimientoCaja tipoMovimiento;
    private BigDecimal monto;
    private String concepto;
    private String referencia;
    private String registradoPor;
    private LocalDateTime fechaMovimiento;
}