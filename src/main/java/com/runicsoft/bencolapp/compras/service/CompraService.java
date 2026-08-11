package com.runicsoft.bencolapp.compras.service;

import com.runicsoft.bencolapp.compras.dtos.request.CompraRequest;
import com.runicsoft.bencolapp.compras.dtos.response.CompraResponse;
import com.runicsoft.bencolapp.compras.utils.EstadoCompra;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;

import java.time.LocalDate;
import java.util.List;

public interface CompraService {
    PaginaResponse<CompraResponse> findAll(int pagina, int tamanio, String codigo, Long proveedorId, EstadoCompra estado, LocalDate desde, LocalDate hasta);
    CompraResponse findById(Long id);
    CompraResponse findByCodigo(String codigo);
    List<CompraResponse> findByProveedorId(Long proveedorId);
    CompraResponse create(CompraRequest request);

    CompraResponse anularCompra(Long id);
}