package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClienteDeudaResponse {
    private Long clienteId;
    private String nombreCliente;
    private BigDecimal deudaTotal;
    private Long cantidadCuentasPendientes;
}