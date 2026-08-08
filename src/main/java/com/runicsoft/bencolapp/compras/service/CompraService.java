package com.runicsoft.bencolapp.compras.service;

import com.runicsoft.bencolapp.compras.dtos.request.CompraRequest;
import com.runicsoft.bencolapp.compras.dtos.response.CompraResponse;

import java.util.List;

public interface CompraService {
    List<CompraResponse> findAll();
    CompraResponse findById(Long id);
    CompraResponse findByCodigo(String codigo);
    List<CompraResponse> findByProveedorId(Long proveedorId);
    CompraResponse create(CompraRequest request);

    CompraResponse anularCompra(Long id);
}