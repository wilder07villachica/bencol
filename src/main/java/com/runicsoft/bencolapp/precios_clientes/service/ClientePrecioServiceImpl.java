package com.runicsoft.bencolapp.precios_clientes.service;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.repository.ClienteRepository;
import com.runicsoft.bencolapp.precios_clientes.dtos.request.ClientePrecioRequest;
import com.runicsoft.bencolapp.precios_clientes.dtos.response.ClientePrecioResponse;
import com.runicsoft.bencolapp.precios_clientes.mapper.ClientePrecioMapper;
import com.runicsoft.bencolapp.precios_clientes.models.ClientePrecio;
import com.runicsoft.bencolapp.precios_clientes.repository.ClientePrecioRepository;
import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.repository.ProductoRepository;
import com.runicsoft.bencolapp.utils.exceptions.ConflictException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class ClientePrecioServiceImpl implements ClientePrecioService {

    private final ClientePrecioRepository clientePrecioRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final ClientePrecioMapper clientePrecioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClientePrecioResponse> findAll() {
        List<ClientePrecio> precios = clientePrecioRepository.findAll();
        return clientePrecioMapper.convertirListaPrecioDto(precios);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientePrecioResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        ClientePrecio clientePrecio = getClientePrecio(id);
        return clientePrecioMapper.convertirDtoEntidad(clientePrecio);
    }

    @Override
    @Transactional
    public ClientePrecioResponse create(ClientePrecioRequest request) {
        Cliente cliente = getCliente(request.getClienteId());
        Producto producto = getProducto(request.getProductoId());

        if (clientePrecioRepository.existsByClienteIdAndProductoId(request.getClienteId(), request.getProductoId())) {
            throw new ConflictException(PRECIO_CLIENTE_EXISTENTE);
        }

        ClientePrecio precio = new ClientePrecio();
        precio.setCliente(cliente);
        precio.setProducto(producto);
        precio.setPrecio(request.getPrecio());

        ClientePrecio precioGuardado = clientePrecioRepository.save(precio);
        return clientePrecioMapper.convertirDtoEntidad(precioGuardado);
    }

    @Override
    @Transactional
    public ClientePrecioResponse update(Long id, ClientePrecioRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        ClientePrecio precio = getClientePrecio(id);
        Cliente cliente = getCliente(request.getClienteId());
        Producto producto = getProducto(request.getProductoId());

        if (clientePrecioRepository.existsByClienteIdAndProductoIdAndIdNot(request.getClienteId(), request.getProductoId(), id)) {
            throw new ConflictException(PRECIO_CLIENTE_EXISTENTE);
        }

        precio.setCliente(cliente);
        precio.setProducto(producto);
        precio.setPrecio(request.getPrecio());

        ClientePrecio precioActualizado = clientePrecioRepository.save(precio);
        return clientePrecioMapper.convertirDtoEntidad(precioActualizado);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        ClientePrecio precio = getClientePrecio(id);
        clientePrecioRepository.delete(precio);
    }

    // metodos auxiliares
    private Cliente getCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CLIENTE_NO_ENCONTRADO));
    }

    private Producto getProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCTO_NO_ENCONTRADO));
    }

    private ClientePrecio getClientePrecio(Long id) {
        return clientePrecioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRECIO_CLIENTE_NO_ENCONTRADO));
    }
}