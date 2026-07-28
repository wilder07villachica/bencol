package com.runicsoft.bencolapp.clientes.service;

import com.runicsoft.bencolapp.clientes.dtos.request.ClienteRequest;
import com.runicsoft.bencolapp.clientes.dtos.response.ClienteResponse;

import java.util.List;

public interface ClienteService {
    List<ClienteResponse> listarClientes();
    ClienteResponse buscarClientePorId(Long id);
    ClienteResponse registrarCliente(ClienteRequest request);
    ClienteResponse actualizarCliente(Long id, ClienteRequest request);
}
