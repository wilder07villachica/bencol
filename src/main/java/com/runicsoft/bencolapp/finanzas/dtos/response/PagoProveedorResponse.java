package com.runicsoft.bencolapp.finanzas.dtos.response;

import com.runicsoft.bencolapp.finanzas.utils.MetodoPago;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoProveedorResponse {
    private Long id;
    private Long cuentaPagarId;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private String referencia;
    private LocalDateTime fechaPago;
}