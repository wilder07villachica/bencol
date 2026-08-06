package com.runicsoft.bencolapp.ventas.mapper;

import com.runicsoft.bencolapp.ventas.dtos.response.DetalleVentaResponse;
import com.runicsoft.bencolapp.ventas.dtos.response.VentaResponse;
import com.runicsoft.bencolapp.ventas.models.DetalleVenta;
import com.runicsoft.bencolapp.ventas.models.Venta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VentaMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nombre", target = "nombreCliente")
    @Mapping(source = "estado", target = "estado")
    VentaResponse convertirVentaDto(Venta venta);

    List<VentaResponse> convertirListaVentaDto(List<Venta> ventas);

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.codigo", target = "codigoProducto")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")
    DetalleVentaResponse convertirDetalleDto(DetalleVenta detalle);
}