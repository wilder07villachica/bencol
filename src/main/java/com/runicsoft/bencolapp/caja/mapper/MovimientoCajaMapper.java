package com.runicsoft.bencolapp.caja.mapper;

import com.runicsoft.bencolapp.caja.dtos.response.MovimientoCajaResponse;
import com.runicsoft.bencolapp.caja.models.MovimientoCaja;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovimientoCajaMapper {

    @Mapping(source = "caja.id", target = "cajaId")
    @Mapping(source = "registradoPor", target = "registradoPor")
    MovimientoCajaResponse convertirMovimientoDto(MovimientoCaja movimiento);

    List<MovimientoCajaResponse> convertirListaMovimientoDto(List<MovimientoCaja> movimientos);
}