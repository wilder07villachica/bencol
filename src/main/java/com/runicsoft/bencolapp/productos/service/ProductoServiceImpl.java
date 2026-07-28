package com.runicsoft.bencolapp.productos.service;

import com.runicsoft.bencolapp.productos.dtos.request.ProductoRequest;
import com.runicsoft.bencolapp.productos.dtos.response.ProductoResponse;
import com.runicsoft.bencolapp.productos.mapper.ProductoMapper;
import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService{

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public List<ProductoResponse> listarProductos() {
        List<Producto>  productos = productoRepository.findAll();
        return productoMapper.convertirListaProductoDto(productos);
    }

    @Override
    public ProductoResponse buscarProductoPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Id invalido");
        }
        Producto producto = productoRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Registro no encontrado")
        );
        return productoMapper.convertirProductoDto(producto);
    }

    @Override
    public ProductoResponse buscarProductoPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código inválido");
        }
        Producto producto = productoRepository.findByCodigo(codigo).orElseThrow(
                () -> new IllegalArgumentException("Registro no encontrado")
        );
        return productoMapper.convertirProductoDto(producto);
    }

    @Override
    public ProductoResponse registrarProducto(ProductoRequest request) {
        if (productoRepository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Registro existente");
        }
        Producto producto = productoRepository.save(productoMapper.convertirProductoEntidad(request));
        return productoMapper.convertirProductoDto(producto);
    }

    @Override
    public ProductoResponse actualizarProducto(Long id, ProductoRequest request) {
        return null;
    }
}
