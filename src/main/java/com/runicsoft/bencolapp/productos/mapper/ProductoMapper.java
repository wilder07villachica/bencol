package com.runicsoft.bencolapp.productos.mapper;

import com.runicsoft.bencolapp.productos.dtos.request.ProductoRequest;
import com.runicsoft.bencolapp.productos.dtos.response.ProductoResponse;
import com.runicsoft.bencolapp.productos.models.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    /*
     * Instancia para convertir una entidad a Dto.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "estado", ignore = true)
    ProductoResponse convertirProductoDto(Producto producto);

    /*
     * Instancia para convertir un Dto. a Entidad
     */
    Producto convertirProductoEntidad(ProductoRequest request);

    /*
     * Instancia para convertir una lista de objetos a Dto.
     */
    List<ProductoResponse> convertirListaProductoDto(List<Producto> productos);
}
