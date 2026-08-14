package com.runicsoft.bencolapp.cotizaciones.dtos.response;

import com.runicsoft.bencolapp.envases.utils.TipoMovimientoEnvase;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleCotizacionResponse {

    private Long id;
    private Long productoId;
    private String codigoProducto;
    private String descripcionProducto;
    private Integer cantidad;
    private TipoMovimientoEnvase modalidadEnvase;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}