package com.runicsoft.bencolapp.clientes.mappers;

import com.runicsoft.bencolapp.clientes.dtos.request.ClienteRequest;
import com.runicsoft.bencolapp.clientes.dtos.response.ClienteResponse;
import com.runicsoft.bencolapp.clientes.models.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteResponse convertirClienteDto(Cliente cliente);

    @Mapping(target = "id", ignore = true)
    Cliente convertirClienteEntidad(ClienteRequest request);

    List<ClienteResponse> convertirListaClienteDto(List<Cliente> clientes);

    @Mapping(target = "id", ignore = true)
    void updateCliente(ClienteRequest request, @MappingTarget Cliente cliente);
}