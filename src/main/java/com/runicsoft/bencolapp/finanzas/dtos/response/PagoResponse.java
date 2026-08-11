package com.runicsoft.bencolapp.finanzas.dtos.response;

import com.runicsoft.bencolapp.finanzas.utils.MetodoPago;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoResponse {
    private Long id;
    private Long cuentaCobrarId;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private String referencia;
    private LocalDateTime fechaPago;
    private String registradoPor;
}