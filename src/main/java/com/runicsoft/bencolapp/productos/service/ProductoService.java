package com.runicsoft.bencolapp.productos.service;

import com.runicsoft.bencolapp.productos.dtos.request.ProductoRequest;
import com.runicsoft.bencolapp.productos.dtos.response.ProductoResponse;
import com.runicsoft.bencolapp.productos.utils.ProductoCategoria;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;

import java.util.List;

public interface ProductoService {
    PaginaResponse<ProductoResponse> findAll(int pagina, int tamanio, String texto, EstadoGeneral estado, ProductoCategoria categoria);
    ProductoResponse findById(Long id);
    ProductoResponse findByCodigo(String codigo);
    ProductoResponse create(ProductoRequest request);
    ProductoResponse update(Long id, ProductoRequest request);
}
