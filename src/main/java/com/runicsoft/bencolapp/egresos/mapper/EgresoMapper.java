package com.runicsoft.bencolapp.egresos.mapper;

import com.runicsoft.bencolapp.egresos.dtos.request.EgresoRequest;
import com.runicsoft.bencolapp.egresos.dtos.response.EgresoResponse;
import com.runicsoft.bencolapp.egresos.models.Egreso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EgresoMapper {

    Egreso convertirEgresoEntidad(EgresoRequest request);

    @Mapping(source = "registradoPor", target = "registradoPor")
    EgresoResponse convertirEgresoDto(Egreso egreso);

    List<EgresoResponse> convertirListaEgresoDto(List<Egreso> egresos);
}