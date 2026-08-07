package com.runicsoft.bencolapp.finanzas.dtos.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DeudaClienteResponse {
    private Long clienteId;
    private String nombreCliente;
    private BigDecimal deudaTotal;
    private Integer cantidadCuentasPendientes;
    private List<CuentaCobrarResponse> cuentas;
}