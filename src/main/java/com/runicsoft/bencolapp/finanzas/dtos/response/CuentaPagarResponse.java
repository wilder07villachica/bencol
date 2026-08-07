package com.runicsoft.bencolapp.finanzas.dtos.response;

import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CuentaPagarResponse {
    private Long id;
    private Long compraId;
    private String codigoCompra;
    private Long proveedorId;
    private String razonSocialProveedor;
    private BigDecimal montoTotal;
    private BigDecimal montoPagado;
    private BigDecimal saldoPendiente;
    private EstadoCuentaPagar estado;
    private List<PagoProveedorResponse> pagos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}