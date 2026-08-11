package com.runicsoft.bencolapp.ventas.service;

import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import com.runicsoft.bencolapp.ventas.dtos.request.VentaRequest;
import com.runicsoft.bencolapp.ventas.dtos.response.VentaResponse;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;

import java.time.LocalDate;
import java.util.List;

public interface VentaService {
    PaginaResponse<VentaResponse> findAll(int pagina, int tamanio, String codigo, Long clienteId, EstadoVenta estado, LocalDate desde, LocalDate hasta);
    VentaResponse findById(Long id);
    VentaResponse findByCodigo(String codigo);
    VentaResponse create(VentaRequest request);
    VentaResponse anularVenta(Long id);
}
