package com.runicsoft.bencolapp.clientes.service;

import com.runicsoft.bencolapp.clientes.dtos.request.ClienteRequest;
import com.runicsoft.bencolapp.clientes.dtos.response.ClienteResponse;

import java.util.List;

public interface ClienteService {
    List<ClienteResponse> findAll();
    ClienteResponse findById(Long id);
    ClienteResponse create(ClienteRequest request);
    ClienteResponse update(Long id, ClienteRequest request);
}
