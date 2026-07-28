package com.runicsoft.bencolapp.empresa.mappers;

import com.runicsoft.bencolapp.empresa.dtos.request.EmpresaRequest;
import com.runicsoft.bencolapp.empresa.dtos.response.EmpresaResponse;
import com.runicsoft.bencolapp.empresa.models.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    /*
    * Instancia para convertir una entidad a Dto.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    EmpresaResponse convertirEmpresaDto(Empresa empresa);

    /*
    * Instancia para convertir un Dto. a Entidad.
     */
    Empresa convertirEmpresaEntidad(EmpresaRequest request);

    /*
    * Instancia para convertir una lista de objetos a Dto.
     */
    List<EmpresaResponse> convertirListaEmpresaDto(List<Empresa> empresas);
}
