package com.runicsoft.bencolapp.productos.service;

import com.runicsoft.bencolapp.productos.dtos.request.ProductoRequest;
import com.runicsoft.bencolapp.productos.dtos.response.ProductoResponse;

import java.util.List;

public interface ProductoService {
    List<ProductoResponse> findAll();
    ProductoResponse findById(Long id);
    ProductoResponse findByCodigo(String codigo);
    ProductoResponse create(ProductoRequest request);
    ProductoResponse update(Long id, ProductoRequest request);
}
