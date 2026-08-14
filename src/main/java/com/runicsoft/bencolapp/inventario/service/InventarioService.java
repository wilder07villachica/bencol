package com.runicsoft.bencolapp.inventario.service;

import com.runicsoft.bencolapp.inventario.dtos.request.InventarioRequest;
import com.runicsoft.bencolapp.inventario.dtos.request.MovimientoInventarioRequest;
import com.runicsoft.bencolapp.inventario.dtos.response.InventarioResponse;
import com.runicsoft.bencolapp.inventario.dtos.response.MovimientoInventarioResponse;
import com.runicsoft.bencolapp.inventario.utils.TipoMovimientoInventario;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;

import java.time.LocalDate;
import java.util.List;

public interface InventarioService {
    PaginaResponse<InventarioResponse> findAll(int pagina, int tamanio);

    PaginaResponse<MovimientoInventarioResponse> findMovimientos(int pagina, int tamanio, Long productoId, TipoMovimientoInventario tipoMovimiento, LocalDate desde, LocalDate hasta);
    InventarioResponse findById(Long id);
    InventarioResponse findByProductoId(Long productoId);
    InventarioResponse create(InventarioRequest request);

    MovimientoInventarioResponse registrarMovimiento(MovimientoInventarioRequest request);
    List<MovimientoInventarioResponse> findMovimientosByProductoId(Long productoId);
}