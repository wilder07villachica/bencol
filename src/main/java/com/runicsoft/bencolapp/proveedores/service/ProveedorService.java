package com.runicsoft.bencolapp.proveedores.service;

import com.runicsoft.bencolapp.proveedores.dtos.request.ProveedorRequest;
import com.runicsoft.bencolapp.proveedores.dtos.response.ProveedorResponse;

import java.util.List;

public interface ProveedorService {
    List<ProveedorResponse> findAll();
    ProveedorResponse findById(Long id);
    ProveedorResponse findByRuc(String ruc);
    ProveedorResponse create(ProveedorRequest request);
    ProveedorResponse update(Long id, ProveedorRequest request);
}