package com.runicsoft.bencolapp.proveedores.mapper;

import com.runicsoft.bencolapp.proveedores.dtos.request.ProveedorRequest;
import com.runicsoft.bencolapp.proveedores.dtos.response.ProveedorResponse;
import com.runicsoft.bencolapp.proveedores.models.Proveedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProveedorMapper {

    ProveedorResponse convertirProveedorDto(Proveedor proveedor);

    Proveedor convertirProveedorEntidad(ProveedorRequest request);

    List<ProveedorResponse> convertirListaProveedorDto(List<Proveedor> proveedores);

    @Mapping(target = "id", ignore = true)
    void updateProveedor(ProveedorRequest request, @MappingTarget Proveedor proveedor);
}