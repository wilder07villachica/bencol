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
import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientePrecioServiceImpl implements ClientePrecioService{

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

        if (clientePrecioRepository.existsByClienteIdAndProductoId(
                request.getClienteId(),
                request.getProductoId()
        )) {
            throw new IllegalArgumentException(PRECIO_CLIENTE_EXISTENTE);
        }

        ClientePrecio precio = new ClientePrecio();

        precio.setCliente(cliente);
        precio.setProducto(producto);
        precio.setPrecio(request.getPrecio());

        ClientePrecio precioNuevo = clientePrecioRepository.save(precio);
        return clientePrecioMapper.convertirDtoEntidad(precioNuevo);
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

        if (clientePrecioRepository.existsByClienteIdAndProductoIdAndIdNot(
                request.getClienteId(), request.getProductoId(), id)
        ) {
            throw new IllegalArgumentException(PRECIO_CLIENTE_EXISTENTE);
        }

        precio.setCliente(cliente);
        precio.setProducto(producto);
        precio.setPrecio(request.getPrecio());

        ClientePrecio actualizado = clientePrecioRepository.save(precio);
        return clientePrecioMapper.convertirDtoEntidad(actualizado);
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

    //Métodos auxiliares
    private Cliente getCliente(Long id) {
        return clienteRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(CLIENTE_NO_ENCONTRADO)
        );
    }

    private Producto getProducto(Long id) {
        return productoRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(PRODUCTO_NO_ENCONTRADO)
        );
    }

    private ClientePrecio getClientePrecio(Long id) {
        return clientePrecioRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(PRECIO_CLIENTE_NO_ENCONTRADO)
        );
    }
}
