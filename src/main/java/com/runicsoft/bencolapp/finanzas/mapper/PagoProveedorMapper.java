package com.runicsoft.bencolapp.finanzas.mapper;

import com.runicsoft.bencolapp.finanzas.dtos.response.PagoProveedorResponse;
import com.runicsoft.bencolapp.finanzas.models.PagoProveedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoProveedorMapper {

    @Mapping(source = "cuentaPagar.id", target = "cuentaPagarId")
    @Mapping(source = "registradoPor", target = "registradoPor")
    PagoProveedorResponse convertirPagoProveedorDto(PagoProveedor pago);

    List<PagoProveedorResponse> convertirListaPagoProveedorDto(List<PagoProveedor> pagos);
}