package com.runicsoft.bencolapp.productos.service;

import com.runicsoft.bencolapp.productos.dtos.request.ProductoRequest;
import com.runicsoft.bencolapp.productos.dtos.response.ProductoResponse;
import com.runicsoft.bencolapp.productos.mapper.ProductoMapper;
import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService{

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductos() {
        List<Producto>  productos = productoRepository.findAll();
        return productoMapper.convertirListaProductoDto(productos);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional
    public ProductoResponse registrarProducto(ProductoRequest request) {
        if (productoRepository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Registro existente");
        }
        Producto producto = productoRepository.save(productoMapper.convertirProductoEntidad(request));
        return productoMapper.convertirProductoDto(producto);
    }

    @Override
    @Transactional
    public ProductoResponse actualizarProducto(Long id, ProductoRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
        Producto producto = productoRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Producto no encontrado")
        );
        producto.setCodigo(request.getCodigo());
        producto.setDescripcion(request.getDescripcion());
        producto.setCategoria(request.getCategoria());
        producto.setPrecioBase(request.getPrecioBase());
        producto.setEstado(request.getEstado());
        productoRepository.save(producto);
        return productoMapper.convertirProductoDto(producto);
    }
}
