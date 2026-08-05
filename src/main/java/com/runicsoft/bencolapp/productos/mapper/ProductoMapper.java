package com.runicsoft.bencolapp.productos.mapper;

import com.runicsoft.bencolapp.productos.dtos.request.ProductoRequest;
import com.runicsoft.bencolapp.productos.dtos.response.ProductoResponse;
import com.runicsoft.bencolapp.productos.models.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    ProductoResponse convertirProductoDto(Producto producto);
    Producto convertirProductoEntidad(ProductoRequest request);
    List<ProductoResponse> convertirListaProductoDto(List<Producto> productos);

    @Mapping(target = "id", ignore = true)
    void updateProducto(ProductoRequest request, @MappingTarget Producto producto);
}
