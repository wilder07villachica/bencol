package com.runicsoft.bencolapp.inventario.service;

import com.runicsoft.bencolapp.inventario.dtos.request.InventarioRequest;
import com.runicsoft.bencolapp.inventario.dtos.request.MovimientoInventarioRequest;
import com.runicsoft.bencolapp.inventario.dtos.response.InventarioResponse;
import com.runicsoft.bencolapp.inventario.dtos.response.MovimientoInventarioResponse;

import java.util.List;

public interface InventarioService {
    List<InventarioResponse> findAll();
    InventarioResponse findById(Long id);
    InventarioResponse findByProductoId(Long productoId);
    InventarioResponse create(InventarioRequest request);

    MovimientoInventarioResponse registrarMovimiento(MovimientoInventarioRequest request);
    List<MovimientoInventarioResponse> findMovimientosByProductoId(Long productoId);
}