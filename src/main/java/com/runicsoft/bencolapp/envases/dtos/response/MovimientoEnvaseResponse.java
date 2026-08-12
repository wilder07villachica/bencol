package com.runicsoft.bencolapp.envases.dtos.response;

import com.runicsoft.bencolapp.envases.utils.TipoMovimientoEnvase;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovimientoEnvaseResponse {
    private Long id;
    private Long cuentaEnvaseId;
    private Long clienteId;
    private String nombreCliente;
    private Long productoId;
    private String codigoProducto;
    private String descripcionProducto;
    private TipoMovimientoEnvase tipoMovimiento;
    private Integer cantidad;
    private String referencia;
    private LocalDateTime fechaMovimiento;
    private String registradoPor;
}