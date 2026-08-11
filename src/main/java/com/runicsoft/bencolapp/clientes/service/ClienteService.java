package com.runicsoft.bencolapp.clientes.service;

import com.runicsoft.bencolapp.clientes.dtos.request.ClienteRequest;
import com.runicsoft.bencolapp.clientes.dtos.response.ClienteResponse;
import com.runicsoft.bencolapp.clientes.utils.CategoriaCliente;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;

import java.util.List;

public interface ClienteService {
    PaginaResponse<ClienteResponse> findAll(int pagina, int tamanio, String texto, EstadoGeneral estado, CategoriaCliente categoria);
    ClienteResponse findById(Long id);
    ClienteResponse create(ClienteRequest request);
    ClienteResponse update(Long id, ClienteRequest request);
}
