package com.runicsoft.bencolapp.proveedores.service;

import com.runicsoft.bencolapp.proveedores.dtos.request.ProveedorRequest;
import com.runicsoft.bencolapp.proveedores.dtos.response.ProveedorResponse;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;

import java.util.List;

public interface ProveedorService {
    PaginaResponse<ProveedorResponse> findAll(int pagina, int tamanio, String texto, EstadoGeneral estado);
    ProveedorResponse findById(Long id);
    ProveedorResponse findByRuc(String ruc);
    ProveedorResponse create(ProveedorRequest request);
    ProveedorResponse update(Long id, ProveedorRequest request);
}