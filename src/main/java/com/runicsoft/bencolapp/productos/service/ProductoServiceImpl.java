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

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService{

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> findAll() {
        List<Producto>  productos = productoRepository.findAll();
        return productoMapper.convertirListaProductoDto(productos);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        Producto producto = getProducto(id);
        return productoMapper.convertirProductoDto(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }
        Producto producto = productoRepository.findByCodigo(codigo).orElseThrow(
                () -> new IllegalArgumentException(PRODUCTO_NO_ENCONTRADO)
        );
        return productoMapper.convertirProductoDto(producto);
    }

    @Override
    @Transactional
    public ProductoResponse create(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException(CODIGO_EXISTENTE);
        }
        Producto producto = productoRepository.save(productoMapper.convertirProductoEntidad(request));
        return productoMapper.convertirProductoDto(producto);
    }

    @Override
    @Transactional
    public ProductoResponse update(Long id, ProductoRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        Producto producto = getProducto(id);

        if (productoRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new IllegalArgumentException(CODIGO_EXISTENTE);
        }

        productoMapper.updateProducto(request, producto);
        productoRepository.save(producto);
        return productoMapper.convertirProductoDto(producto);
    }
    
    // Métodos auxiliares
    private Producto getProducto(Long id) {
        return productoRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(PRODUCTO_NO_ENCONTRADO)
        );
    }
}
