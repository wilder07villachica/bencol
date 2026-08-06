package com.runicsoft.bencolapp.inventario.mapper;

import com.runicsoft.bencolapp.inventario.dtos.response.InventarioResponse;
import com.runicsoft.bencolapp.inventario.models.Inventario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventarioMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.codigo", target = "codigoProducto")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")
    InventarioResponse convertirInventarioDto(Inventario inventario);

    List<InventarioResponse> convertirListaInventarioDto(List<Inventario> inventarios);
}