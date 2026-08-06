package com.runicsoft.bencolapp.ventas.service;

import com.runicsoft.bencolapp.ventas.dtos.request.VentaRequest;
import com.runicsoft.bencolapp.ventas.dtos.response.VentaResponse;

import java.util.List;

public interface VentaService {
    List<VentaResponse> findAll();
    VentaResponse findById(Long id);
    VentaResponse findByCodigo(String codigo);
    VentaResponse create(VentaRequest request);
    VentaResponse anularVenta(Long id);
}
