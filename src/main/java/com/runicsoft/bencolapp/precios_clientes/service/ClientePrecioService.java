package com.runicsoft.bencolapp.precios_clientes.service;

import com.runicsoft.bencolapp.precios_clientes.dtos.request.ClientePrecioRequest;
import com.runicsoft.bencolapp.precios_clientes.dtos.response.ClientePrecioResponse;

import java.util.List;

public interface ClientePrecioService {
    List<ClientePrecioResponse> findAll();
    ClientePrecioResponse findById(Long id);
    ClientePrecioResponse create(ClientePrecioRequest request);
    ClientePrecioResponse update(Long id, ClientePrecioRequest request);
    void deleteById(Long id);
}
