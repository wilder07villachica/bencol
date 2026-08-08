package com.runicsoft.bencolapp.compras.dtos.response;

import com.runicsoft.bencolapp.compras.utils.EstadoCompra;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CompraResponse {
    private Long id;
    private String codigo;
    private Long proveedorId;
    private String razonSocialProveedor;
    private BigDecimal subtotal;
    private BigDecimal total;
    private EstadoCompra estado;
    private List<DetalleCompraResponse> detalles;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}