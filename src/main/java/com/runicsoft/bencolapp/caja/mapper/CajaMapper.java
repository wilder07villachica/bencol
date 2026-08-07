package com.runicsoft.bencolapp.caja.mapper;

import com.runicsoft.bencolapp.caja.dtos.response.CajaResponse;
import com.runicsoft.bencolapp.caja.models.Caja;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = MovimientoCajaMapper.class
)
public interface CajaMapper {

    CajaResponse convertirCajaDto(Caja caja);

    List<CajaResponse> convertirListaCajaDto(List<Caja> cajas);
}