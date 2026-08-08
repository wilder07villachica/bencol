package com.runicsoft.bencolapp.finanzas.dtos.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DeudaProveedorResponse {
    private Long proveedorId;
    private String razonSocialProveedor;
    private BigDecimal deudaTotal;
    private Integer cantidadCuentasPendientes;
    private List<CuentaPagarResponse> cuentas;
}