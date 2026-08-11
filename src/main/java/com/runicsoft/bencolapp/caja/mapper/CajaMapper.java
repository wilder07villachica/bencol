package com.runicsoft.bencolapp.caja.mapper;

import com.runicsoft.bencolapp.caja.dtos.response.CajaResponse;
import com.runicsoft.bencolapp.caja.models.Caja;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = MovimientoCajaMapper.class
)
public interface CajaMapper {

    @Mapping(source = "abiertaPor", target = "abiertaPor")
    @Mapping(source = "cerradaPor", target = "cerradaPor")
    CajaResponse convertirCajaDto(Caja caja);

    List<CajaResponse> convertirListaCajaDto(List<Caja> cajas);
}