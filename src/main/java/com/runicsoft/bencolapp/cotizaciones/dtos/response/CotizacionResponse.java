package com.runicsoft.bencolapp.cotizaciones.dtos.response;

import com.runicsoft.bencolapp.cotizaciones.utils.EstadoCotizacion;
import com.runicsoft.bencolapp.cotizaciones.utils.TipoPrecioCotizacion;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CotizacionResponse {
    private Long id;
    private String codigo;
    private Long empresaId;
    private String rucEmpresa;
    private String razonSocialEmpresa;
    private String nombreComercialEmpresa;
    private Long clienteId;
    private String nombreCliente;
    private BigDecimal subtotal;
    private BigDecimal porcentajeImpuesto;
    private BigDecimal montoImpuesto;
    private BigDecimal total;
    private EstadoCotizacion estado;
    private LocalDate fechaVencimiento;
    private String condicionesPago;
    private String plazoEntrega;
    private String observacion;
    private List<DetalleCotizacionResponse> detalles;
    private TipoPrecioCotizacion tipoPrecio;
    private String creadoPor;
    private String actualizadoPor;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}