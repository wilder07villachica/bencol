package com.runicsoft.bencolapp.entregas.mapper;

import com.runicsoft.bencolapp.entregas.dtos.response.EntregaResponse;
import com.runicsoft.bencolapp.entregas.dtos.response.HistorialEntregaResponse;
import com.runicsoft.bencolapp.entregas.dtos.response.ProductoEntregaResponse;
import com.runicsoft.bencolapp.entregas.dtos.response.ResponsableEntregaResponse;
import com.runicsoft.bencolapp.entregas.models.Entrega;
import com.runicsoft.bencolapp.entregas.models.HistorialEntrega;
import com.runicsoft.bencolapp.seguridad.models.Usuario;
import com.runicsoft.bencolapp.ventas.models.DetalleVenta;
import org.springframework.stereotype.Component;

@Component
public class EntregaMapper {

    public EntregaResponse convertirEntregaDto(Entrega entrega) {
        EntregaResponse response = new EntregaResponse();

        var venta = entrega.getVenta();
        var cliente = venta.getCliente();
        var repartidor = entrega.getRepartidor();

        response.setId(entrega.getId());
        response.setVentaId(venta.getId());
        response.setCodigoVenta(venta.getCodigo());
        response.setEstadoVenta(venta.getEstado());

        response.setClienteId(cliente.getId());
        response.setNombreCliente(cliente.getNombre());
        response.setTelefonoCliente(cliente.getTelefono());
        response.setDireccion(entrega.getDireccion());

        response.setRepartidorId(repartidor == null ? null : repartidor.getId());

        response.setRepartidor(
                repartidor == null ? null : repartidor.getUsername()
        );

        response.setEstado(entrega.getEstado());
        response.setObservaciones(entrega.getObservaciones());

        response.setFechaCreacion(entrega.getFechaCreacion());
        response.setFechaActualizacion(entrega.getFechaActualizacion());
        response.setFechaProgramada(entrega.getFechaProgramada());
        response.setFechaEntrega(entrega.getFechaEntrega());

        response.setVersion(entrega.getVersion());

        response.setProductos(
                venta.getDetalles()
                        .stream()
                        .map(this::convertirProductoDto)
                        .toList()
        );

        return response;
    }

    public ProductoEntregaResponse convertirProductoDto(
            DetalleVenta detalle
    ) {
        ProductoEntregaResponse response =
                new ProductoEntregaResponse();

        response.setProductoId(detalle.getProducto().getId());
        response.setCodigo(detalle.getProducto().getCodigo());
        response.setDescripcion(detalle.getProducto().getDescripcion());
        response.setCantidad(detalle.getCantidad());

        return response;
    }

    public ResponsableEntregaResponse convertirResponsableDto(
            Usuario usuario
    ) {
        ResponsableEntregaResponse response =
                new ResponsableEntregaResponse();

        response.setId(usuario.getId());
        response.setUsername(usuario.getUsername());
        response.setRol(usuario.getRol());

        return response;
    }

    public HistorialEntregaResponse convertirHistorialDto(
            HistorialEntrega historial
    ) {
        HistorialEntregaResponse response =
                new HistorialEntregaResponse();

        response.setId(historial.getId());
        response.setAccion(historial.getAccion());
        response.setEstadoAnterior(historial.getEstadoAnterior());
        response.setEstadoNuevo(historial.getEstadoNuevo());
        response.setObservaciones(historial.getObservaciones());
        response.setUsuario(historial.getUsuario());
        response.setFecha(historial.getFecha());

        return response;
    }
}