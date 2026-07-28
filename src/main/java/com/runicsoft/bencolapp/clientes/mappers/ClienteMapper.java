package com.runicsoft.bencolapp.clientes.mappers;

import com.runicsoft.bencolapp.clientes.dtos.request.ClienteRequest;
import com.runicsoft.bencolapp.clientes.dtos.response.ClienteResponse;
import com.runicsoft.bencolapp.clientes.models.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    /*
     * Instancia para convertir una entidad a Dto.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "estado", ignore = true)
    ClienteResponse convertirClienteDto(Cliente cliente);

    /*
     * Instancia para convertir un Dto. a Entidad.
     */
    Cliente convertirClienteEntidad(ClienteRequest request);

    /*
     * Instancia para convertir una lista de objetos a Dto.
     */
    List<ClienteResponse> convertirListaClienteDto(List<Cliente> clientes);
}
