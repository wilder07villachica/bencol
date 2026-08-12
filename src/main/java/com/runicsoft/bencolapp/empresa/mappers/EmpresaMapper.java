package com.runicsoft.bencolapp.empresa.mappers;

import com.runicsoft.bencolapp.empresa.dtos.request.EmpresaRequest;
import com.runicsoft.bencolapp.empresa.dtos.response.EmpresaResponse;
import com.runicsoft.bencolapp.empresa.models.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    EmpresaResponse convertirEmpresaDto(Empresa empresa);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Empresa convertirEmpresaEntidad(EmpresaRequest request);

    List<EmpresaResponse> convertirListaEmpresaDto(List<Empresa> empresas);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEmpresa(EmpresaRequest request, @MappingTarget Empresa empresa);
}