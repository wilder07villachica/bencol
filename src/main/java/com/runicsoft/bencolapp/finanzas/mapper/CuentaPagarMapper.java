package com.runicsoft.bencolapp.finanzas.mapper;

import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaPagarResponse;
import com.runicsoft.bencolapp.finanzas.models.CuentaPagar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = PagoProveedorMapper.class
)
public interface CuentaPagarMapper {

    @Mapping(source = "compra.id", target = "compraId")
    @Mapping(source = "compra.codigo", target = "codigoCompra")
    @Mapping(source = "compra.proveedor.id", target = "proveedorId")
    @Mapping(source = "compra.proveedor.razonSocial", target = "razonSocialProveedor")
    CuentaPagarResponse convertirCuentaPagarDto(CuentaPagar cuenta);

    List<CuentaPagarResponse> convertirListaCuentaPagarDto(List<CuentaPagar> cuentas);
}