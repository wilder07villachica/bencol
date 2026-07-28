package com.runicsoft.bencolapp.productos.service;

import com.runicsoft.bencolapp.productos.dtos.request.ProductoRequest;
import com.runicsoft.bencolapp.productos.dtos.response.ProductoResponse;

import java.util.List;

public interface ProductoService {
    List<ProductoResponse> listarProductos();
    ProductoResponse buscarProductoPorId(Long id);
    ProductoResponse buscarProductoPorCodigo(String codigo);
    ProductoResponse registrarProducto(ProductoRequest request);
    ProductoResponse actualizarProducto(Long id, ProductoRequest request);
}
