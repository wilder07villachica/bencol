package com.runicsoft.bencolapp.compras.mapper;

import com.runicsoft.bencolapp.compras.dtos.response.CompraResponse;
import com.runicsoft.bencolapp.compras.dtos.response.DetalleCompraResponse;
import com.runicsoft.bencolapp.compras.models.Compra;
import com.runicsoft.bencolapp.compras.models.DetalleCompra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompraMapper {

    @Mapping(source = "proveedor.id", target = "proveedorId")
    @Mapping(source = "proveedor.razonSocial", target = "razonSocialProveedor")
    @Mapping(source = "creadoPor", target = "creadoPor")
    @Mapping(source = "actualizadoPor", target = "actualizadoPor")
    CompraResponse convertirCompraDto(Compra compra);

    List<CompraResponse> convertirListaCompraDto(List<Compra> compras);

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.codigo", target = "codigoProducto")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")
    DetalleCompraResponse convertirDetalleDto(DetalleCompra detalle);
}