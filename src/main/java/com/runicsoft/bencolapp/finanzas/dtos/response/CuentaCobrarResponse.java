package com.runicsoft.bencolapp.finanzas.dtos.response;

import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CuentaCobrarResponse {
    private Long id;
    private Long ventaId;
    private String codigoVenta;
    private Long clienteId;
    private String nombreCliente;
    private BigDecimal montoTotal;
    private BigDecimal montoPagado;
    private BigDecimal saldoPendiente;
    private EstadoCuenta estado;
    private List<PagoResponse> pagos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}