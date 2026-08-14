package com.runicsoft.bencolapp.cotizaciones.dtos.response;

import com.runicsoft.bencolapp.cotizaciones.utils.EstadoCotizacion;
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
    private BigDecimal total;

    private EstadoCotizacion estado;

    private LocalDate fechaVencimiento;
    private String observacion;

    private String imagenNombre;
    private String imagenTipo;
    private Boolean tieneImagen;

    private List<DetalleCotizacionResponse> detalles;

    private String creadoPor;
    private String actualizadoPor;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}