package com.runicsoft.bencolapp.finanzas.mapper;

import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaCobrarResponse;
import com.runicsoft.bencolapp.finanzas.models.CuentaCobrar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = PagoMapper.class
)
public interface CuentaCobrarMapper {

    @Mapping(source = "venta.id", target = "ventaId")
    @Mapping(source = "venta.codigo", target = "codigoVenta")
    @Mapping(source = "venta.cliente.id", target = "clienteId")
    @Mapping(source = "venta.cliente.nombre", target = "nombreCliente")
    CuentaCobrarResponse convertirCuentaDto(CuentaCobrar cuentaCobrar);

    List<CuentaCobrarResponse> convertirListaCuentaDto(List<CuentaCobrar> cuentas);
}