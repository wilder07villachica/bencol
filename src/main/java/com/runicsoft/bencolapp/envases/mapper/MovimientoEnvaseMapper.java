package com.runicsoft.bencolapp.envases.mapper;

import com.runicsoft.bencolapp.envases.dtos.response.MovimientoEnvaseResponse;
import com.runicsoft.bencolapp.envases.models.MovimientoEnvase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovimientoEnvaseMapper {

    @Mapping(source = "cuentaEnvase.id", target = "cuentaEnvaseId")
    @Mapping(source = "cuentaEnvase.cliente.id", target = "clienteId")
    @Mapping(source = "cuentaEnvase.cliente.nombre", target = "nombreCliente")
    @Mapping(source = "cuentaEnvase.producto.id", target = "productoId")
    @Mapping(source = "cuentaEnvase.producto.codigo", target = "codigoProducto")
    @Mapping(source = "cuentaEnvase.producto.descripcion", target = "descripcionProducto")
    MovimientoEnvaseResponse convertirMovimientoDto(MovimientoEnvase movimiento);

    List<MovimientoEnvaseResponse> convertirListaMovimientoDto(List<MovimientoEnvase> movimientos);
}