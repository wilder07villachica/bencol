package com.runicsoft.bencolapp.inventario.mapper;

import com.runicsoft.bencolapp.inventario.dtos.response.MovimientoInventarioResponse;
import com.runicsoft.bencolapp.inventario.models.MovimientoInventario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovimientoInventarioMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.codigo", target = "codigoProducto")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")
    @Mapping(source = "registradoPor", target = "registradoPor")
    MovimientoInventarioResponse convertirMovimientoDto(MovimientoInventario movimiento);

    List<MovimientoInventarioResponse> convertirListaMovimientoDto(List<MovimientoInventario> movimientos);
}