package com.runicsoft.bencolapp.ventas.dtos.response;

import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaResponse {
    private Long id;
    private String codigo;
    private Long clienteId;
    private String nombreCliente;
    private BigDecimal subtotal;
    private BigDecimal total;
    private List<DetalleVentaResponse> detalles;
    private EstadoVenta estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}