package com.runicsoft.bencolapp.precios_clientes.mapper;

import com.runicsoft.bencolapp.precios_clientes.dtos.request.ClientePrecioRequest;
import com.runicsoft.bencolapp.precios_clientes.dtos.response.ClientePrecioResponse;
import com.runicsoft.bencolapp.precios_clientes.models.ClientePrecio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientePrecioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "producto", ignore = true)
    ClientePrecio convertirClientePrecioDto(ClientePrecioRequest request);

    @Mapping(source = "cliente.nombre", target = "nombreCliente")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")
    ClientePrecioResponse convertirDtoEntidad(ClientePrecio clientePrecio);

    List<ClientePrecioResponse> convertirListaPrecioDto(List<ClientePrecio> clientePrecios);
}
