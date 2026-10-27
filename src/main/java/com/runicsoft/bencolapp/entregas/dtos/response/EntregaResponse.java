package com.runicsoft.bencolapp.entregas.dtos.response;

import com.runicsoft.bencolapp.entregas.utils.EstadoEntrega;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EntregaResponse {

    private Long id;
    private Long ventaId;
    private String codigoVenta;
    private EstadoVenta estadoVenta;

    private Long clienteId;
    private String nombreCliente;
    private String telefonoCliente;
    private String direccion;

    private Long repartidorId;
    private String repartidor;

    private EstadoEntrega estado;
    private String observaciones;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaEntrega;

    private Long version;

    private List<ProductoEntregaResponse> productos;
}
