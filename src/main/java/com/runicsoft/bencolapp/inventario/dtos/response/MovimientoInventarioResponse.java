package com.runicsoft.bencolapp.inventario.dtos.response;

import com.runicsoft.bencolapp.inventario.utils.TipoMovimientoInventario;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovimientoInventarioResponse {
    private Long id;
    private Long productoId;
    private String codigoProducto;
    private String descripcionProducto;
    private TipoMovimientoInventario tipoMovimiento;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private String referencia;
    private String registradoPor;
    private LocalDateTime fechaCreacion;
}