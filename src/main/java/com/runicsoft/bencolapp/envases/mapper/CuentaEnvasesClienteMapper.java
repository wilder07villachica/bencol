package com.runicsoft.bencolapp.envases.mapper;

import com.runicsoft.bencolapp.envases.dtos.response.CuentaEnvasesClienteResponse;
import com.runicsoft.bencolapp.envases.models.CuentaEnvasesCliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CuentaEnvasesClienteMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nombre", target = "nombreCliente")
    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.codigo", target = "codigoProducto")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")
    @Mapping(
            target = "cantidadTotal",
            expression = "java(cuenta.getCantidadPropios() + cuenta.getCantidadPrestados())"
    )
    CuentaEnvasesClienteResponse convertirCuentaDto(CuentaEnvasesCliente cuenta);

    List<CuentaEnvasesClienteResponse> convertirListaCuentaDto(List<CuentaEnvasesCliente> cuentas);
}