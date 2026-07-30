package com.runicsoft.bencolapp.precios_clientes.service;

import com.runicsoft.bencolapp.precios_clientes.dtos.request.ClientePrecioRequest;
import com.runicsoft.bencolapp.precios_clientes.dtos.response.ClientePrecioResponse;

import java.util.List;

public interface ClientePrecioService {
    List<ClientePrecioResponse> listarPreciosClientes();
    ClientePrecioResponse buscarPrecioPorId(Long id);
    ClientePrecioResponse registrarNuevoPrecio(ClientePrecioRequest request);
    ClientePrecioResponse actualizarInformacion(Long id, ClientePrecioRequest request);
    void deleteClientePrecio(Long id);
}
