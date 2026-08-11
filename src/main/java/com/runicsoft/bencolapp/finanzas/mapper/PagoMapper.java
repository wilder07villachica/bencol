package com.runicsoft.bencolapp.finanzas.mapper;

import com.runicsoft.bencolapp.finanzas.dtos.response.PagoResponse;
import com.runicsoft.bencolapp.finanzas.models.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    @Mapping(source = "cuentaCobrar.id", target = "cuentaCobrarId")
    @Mapping(source = "registradoPor", target = "registradoPor")
    PagoResponse convertirPagoDto(Pago pago);

    List<PagoResponse> convertirListaPagoDto(List<Pago> pagos);
}