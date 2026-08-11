package com.runicsoft.bencolapp.precios_clientes.service;

import com.runicsoft.bencolapp.precios_clientes.dtos.request.ClientePrecioRequest;
import com.runicsoft.bencolapp.precios_clientes.dtos.response.ClientePrecioResponse;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;

import java.util.List;

public interface ClientePrecioService {
    PaginaResponse<ClientePrecioResponse> findAll(int pagina, int tamanio, Long clienteId, Long productoId);
    ClientePrecioResponse findById(Long id);
    ClientePrecioResponse create(ClientePrecioRequest request);
    ClientePrecioResponse update(Long id, ClientePrecioRequest request);
    void deleteById(Long id);
}
